package microarch.delivery.core.domain.model.courier;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Guard;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Courier — агрегат «Курьер».
 * <p>
 * Бизнес-правила:
 * <ul>
 * <li>курьер создается с Name и Location, MaxVolume = 20 литров;</li>
 * <li>курьер может взять новый заказ, если сумма объемов всех его заказов с учетом нового не превышает MaxVolume;</li>
 * <li>при взятии заказа создается Assignment с Id заказа (смену статуса заказа координирует use case или доменный
 * сервис — агрегаты не изменяют друг друга);</li>
 * <li>курьер может завершить Assignment, только если находится в 1 клетке от заказа или ближе;</li>
 * <li>курьер может переместиться только на 1 шаг от своего Location.</li>
 * </ul>
 */
@Entity
@Table(name = "couriers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class Courier extends Aggregate<UUID> {
    public static final int MAX_VOLUME_VALUE = 20;

    @Column(name = "name", nullable = false)
    private String name;

    @Embedded
    private Location location;

    @Embedded
    private Volume maxVolume;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "courier_id", nullable = false)
    private final List<Assignment> assignments = new ArrayList<>();

    private Courier(UUID id, String name, Location location) {
        super(id);
        this.name = name;
        this.location = location;
        this.maxVolume = Volume.mustCreate(MAX_VOLUME_VALUE);
    }

    public static Result<Courier, Error> create(String name, Location location) {
        var err = Guard.combine(Guard.againstNullOrEmpty(name, "name"),
                location == null ? GeneralErrors.valueIsRequired("location") : null);
        if (err != null)
            return Result.failure(err);

        return Result.success(new Courier(UUID.randomUUID(), name, location));
    }

    public static Courier mustCreate(String name, Location location) {
        return create(name, location).getValueOrThrow();
    }

    /**
     * Суммарный объем заказов, уже взятых курьером.
     */
    public Volume getTakenVolume() {
        return assignments.stream().map(Assignment::getOrderVolume).reduce(Volume.ZERO, Volume::add);
    }

    /**
     * Может ли курьер взять еще один заказ с указанным объемом?
     */
    public boolean canTakeOrder(Volume orderVolume) {
        if (orderVolume == null)
            return false;
        return getTakenVolume().add(orderVolume).isLessOrEqual(this.maxVolume);
    }

    /**
     * Добавить назначение на заказ. Курьер управляет только своими назначениями; смену статуса самого заказа
     * координирует use case или доменный сервис.
     */
    public UnitResult<Error> addAssignment(UUID orderId, Volume orderVolume) {
        var err = Guard.combine(Guard.againstNullOrEmpty(orderId, "orderId"),
                orderVolume == null ? GeneralErrors.valueIsRequired("orderVolume") : null);
        if (err != null)
            return UnitResult.failure(err);

        if (!canTakeOrder(orderVolume))
            return UnitResult.failure(Errors.courierCannotTakeOrder(this.name, orderId));

        this.assignments.add(Assignment.mustCreate(orderId, orderVolume));
        return UnitResult.success();
    }

    /**
     * Есть ли у курьера назначение на указанный заказ?
     */
    public boolean hasAssignment(UUID orderId) {
        return findAssignment(orderId).isPresent();
    }

    /**
     * Находится ли курьер в 1 клетке от указанного местоположения или ближе?
     */
    public boolean isWithinReach(Location location) {
        if (location == null)
            return false;
        return this.location.distanceTo(location) <= 1;
    }

    /**
     * Снять назначение по заказу (например, после завершения доставки).
     */
    public UnitResult<Error> removeAssignment(UUID orderId) {
        var assignment = findAssignment(orderId);
        if (assignment.isEmpty())
            return UnitResult.failure(Errors.assignmentNotFound(this.name, orderId));

        this.assignments.remove(assignment.get());
        return UnitResult.success();
    }

    /**
     * Переместиться в указанный Location, но не дальше чем на 1 шаг.
     */
    public UnitResult<Error> move(Location target) {
        if (target == null)
            return UnitResult.failure(GeneralErrors.valueIsRequired("target"));

        if (this.location.distanceTo(target) > 1)
            return UnitResult.failure(Errors.targetIsTooFar(this.name, this.location, target));

        this.location = target;
        return UnitResult.success();
    }

    private Optional<Assignment> findAssignment(UUID orderId) {
        return assignments.stream().filter(a -> a.getOrderId().equals(orderId)).findFirst();
    }

    public static class Errors {
        public static Error courierCannotTakeOrder(String courierName, UUID orderId) {
            return Error.of("courier.cannot.take.order", String
                    .format("Курьер %s не может взять заказ %s: недостаточно свободного объема", courierName, orderId));
        }

        public static Error assignmentNotFound(String courierName, UUID orderId) {
            return Error.of("courier.assignment.not.found",
                    String.format("У курьера %s нет назначения на заказ %s", courierName, orderId));
        }

        public static Error courierTooFarFromOrder(String courierName, UUID orderId) {
            return Error.of("courier.too.far.from.order",
                    String.format("Курьер %s не может завершить заказ %s: он находится дальше 1 клетки от заказа",
                            courierName, orderId));
        }

        public static Error targetIsTooFar(String courierName, Location current, Location target) {
            return Error.of("courier.move.too.far",
                    String.format("Курьер %s не может переместиться из %s в %s: расстояние больше 1 шага", courierName,
                            current, target));
        }
    }
}
