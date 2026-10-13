package microarch.delivery.core.domain.services;

import libs.errs.Error;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Тесты алгоритма диспетчеризации: распределение заказа в статусе CREATED на ближайшего свободного курьера.
 */
class CourierOrderServiceDispatchTest {

    private final CourierOrderService service = new CourierOrderServiceImpl();

    private final Location orderLocation = Location.mustCreate(5, 5);

    private Order createOrder(int volumeValue) {
        return Order.mustCreate(UUID.randomUUID(), orderLocation, Volume.mustCreate(volumeValue));
    }

    /* ---------- выбор курьера ---------- */

    @Test
    void shouldDispatchToClosestCourier() {
        // Arrange
        var far = Courier.mustCreate("Далёкий", Location.mustCreate(1, 1));
        var close = Courier.mustCreate("Близкий", Location.mustCreate(6, 5));
        var middle = Courier.mustCreate("Средний", Location.mustCreate(3, 5));
        var order = createOrder(5);

        // Act
        var result = service.dispatch(order, List.of(far, close, middle));

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isSameAs(close);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
        assertThat(close.getAssignments()).hasSize(1);
        assertThat(far.getAssignments()).isEmpty();
        assertThat(middle.getAssignments()).isEmpty();
    }

    @Test
    void shouldDispatchToSingleCourier() {
        // Arrange
        var courier = Courier.mustCreate("Иван", Location.mustCreate(10, 10));
        var order = createOrder(5);

        // Act
        var result = service.dispatch(order, List.of(courier));

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isSameAs(courier);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
        assertThat(courier.getTakenVolume().getValue()).isEqualTo(5);
    }

    @Test
    void shouldSkipOverloadedCouriers() {
        // Arrange
        // Ближайший курьер переполнен — заказ должен уйти следующему по близости
        var overloaded = Courier.mustCreate("Переполненный", Location.mustCreate(5, 6));
        service.assignOrder(overloaded, Order.mustCreate(UUID.randomUUID(), orderLocation, Volume.mustCreate(20)));
        var free = Courier.mustCreate("Свободный", Location.mustCreate(1, 1));
        var order = createOrder(5);

        // Act
        var result = service.dispatch(order, List.of(overloaded, free));

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isSameAs(free);
        assertThat(overloaded.getAssignments()).hasSize(1);
        assertThat(free.getAssignments()).hasSize(1);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
    }

    @Test
    void shouldAssignOrderUpToExactlyMaxVolume() {
        // Arrange
        var courier = Courier.mustCreate("Иван", orderLocation);
        service.assignOrder(courier, Order.mustCreate(UUID.randomUUID(), orderLocation, Volume.mustCreate(10)));
        service.assignOrder(courier, Order.mustCreate(UUID.randomUUID(), orderLocation, Volume.mustCreate(5)));
        var order = createOrder(5);

        // Act
        var result = service.dispatch(order, List.of(courier));

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.getTakenVolume().getValue()).isEqualTo(20);
    }

    /* ---------- согласованные состояние ---------- */

    @Test
    void shouldBringCourierAndOrderToConsistentState() {
        // Arrange
        var courier = Courier.mustCreate("Иван", Location.mustCreate(2, 3));
        var order = createOrder(7);

        // Act
        var result = service.dispatch(order, List.of(courier));

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
        assertThat(courier.getAssignments()).hasSize(1);
        assertThat(courier.getAssignments().get(0).getOrderId()).isEqualTo(order.getId());
        assertThat(courier.getAssignments().get(0).getOrderVolume().getValue()).isEqualTo(7);
        assertThat(courier.getTakenVolume().getValue()).isEqualTo(7);
    }

    /* ---------- бизнес-ошибки ---------- */

    @Test
    void shouldReturnErrorWhenAllCouriersAreOverloaded() {
        // Arrange
        var c1 = Courier.mustCreate("Курьер 1", orderLocation);
        service.assignOrder(c1, Order.mustCreate(UUID.randomUUID(), orderLocation, Volume.mustCreate(20)));
        var c2 = Courier.mustCreate("Курьер 2", orderLocation);
        service.assignOrder(c2, Order.mustCreate(UUID.randomUUID(), orderLocation, Volume.mustCreate(15)));
        var order = createOrder(10);

        // Act
        var result = service.dispatch(order, List.of(c1, c2));

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError().getCode()).isEqualTo("dispatch.no.available.courier");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(c1.getAssignments()).hasSize(1);
        assertThat(c2.getAssignments()).hasSize(1);
    }

    @Test
    void shouldReturnErrorWhenCourierListIsEmpty() {
        // Act
        var result = service.dispatch(createOrder(5), List.of());

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError().getCode()).isEqualTo("dispatch.no.available.courier");
    }

    @Test
    void shouldReturnErrorWhenCourierListIsNull() {
        // Act
        var result = service.dispatch(createOrder(5), null);

        // Assert
        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void shouldThrowWhenCourierListContainsNullElement() {
        // Arrange: null в списке — нарушение контракта (баг вызывающего кода),
        // а не бизнес-ошибка, поэтому fail-fast через исключение
        var courier = Courier.mustCreate("Иван", orderLocation);

        // Act / Assert
        assertThatThrownBy(() -> service.dispatch(createOrder(5), java.util.Arrays.asList(null, courier)))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("null elements");
    }

    @Test
    void shouldReturnErrorWhenOrderIsNull() {
        // Arrange
        var courier = Courier.mustCreate("Иван", orderLocation);

        // Act
        var result = service.dispatch(null, List.of(courier));

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError().getCode()).isEqualTo("value.is.required");
    }

    @Test
    void shouldReturnErrorWhenOrderIsNotInCreatedStatus() {
        // Arrange
        var courier = Courier.mustCreate("Иван", orderLocation);
        var order = createOrder(5);
        service.dispatch(order, List.of(courier)); // заказ уже ASSIGNED

        var other = Courier.mustCreate("Пётр", orderLocation);

        // Act
        var result = service.dispatch(order, List.of(other));

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError().getCode()).isEqualTo("order.has.wrong.status");
        assertThat(other.getAssignments()).isEmpty();
    }

    /* ---------- граничный случай: равные расстояния ---------- */

    @Test
    void shouldPickFirstCourierWhenDistancesAreEqual() {
        // Arrange: оба курьера на расстоянии 1 от заказа
        var first = Courier.mustCreate("Первый", Location.mustCreate(5, 4));
        var second = Courier.mustCreate("Второй", Location.mustCreate(4, 5));
        var order = createOrder(5);

        // Act
        var result = service.dispatch(order, List.of(first, second));

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isSameAs(first);
    }
}
