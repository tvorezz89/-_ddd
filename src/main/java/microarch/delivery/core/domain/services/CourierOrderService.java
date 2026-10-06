package microarch.delivery.core.domain.services;

import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;

/**
 * Доменный сервис, координирующий агрегаты Courier и Order.
 * <p>
 * Агрегаты не должны изменять состояние друг друга, поэтому сценарии «назначить заказ курьеру» и «завершить доставку
 * заказа», затрагивающие две границы консистентности, реализованы здесь. Каждый агрегат при этом инкапсулирует только
 * свои инварианты:
 * <ul>
 * <li>{@link Order} — допустимые переходы статусов;</li>
 * <li>{@link Courier} — ограничение по объему и наличие назначения.</li>
 * </ul>
 */
public final class CourierOrderService {

    private CourierOrderService() {
        // Domain service, no instantiation
    }

    /**
     * Назначить заказ курьеру: переводит заказ в ASSIGNED и создает у курьера Assignment.
     */
    public static UnitResult<Error> assignOrder(Courier courier, Order order) {
        if (courier == null)
            return UnitResult.failure(GeneralErrors.valueIsRequired("courier"));
        if (order == null)
            return UnitResult.failure(GeneralErrors.valueIsRequired("order"));

        if (!courier.canTakeOrder(order.getVolume()))
            return UnitResult.failure(Courier.Errors.courierCannotTakeOrder(courier.getName(), order.getId()));

        var assignResult = order.assign();
        if (assignResult.isFailure())
            return assignResult;

        return courier.addAssignment(order.getId(), order.getVolume());
    }

    /**
     * Завершить доставку заказа: проверяет, что заказ назначен этому курьеру и курьер находится в 1 клетке от заказа
     * или ближе, затем завершает заказ и снимает назначение.
     */
    public static UnitResult<Error> completeOrder(Courier courier, Order order) {
        if (courier == null)
            return UnitResult.failure(GeneralErrors.valueIsRequired("courier"));
        if (order == null)
            return UnitResult.failure(GeneralErrors.valueIsRequired("order"));

        if (!courier.hasAssignment(order.getId()))
            return UnitResult.failure(Courier.Errors.assignmentNotFound(courier.getName(), order.getId()));

        if (!courier.isWithinReach(order.getLocation()))
            return UnitResult.failure(Courier.Errors.courierTooFarFromOrder(courier.getName(), order.getId()));

        var completeResult = order.complete();
        if (completeResult.isFailure())
            return completeResult;

        return courier.removeAssignment(order.getId());
    }
}
