package microarch.delivery.core.domain.services;

import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;

import java.util.List;

/**
 * Доменный сервис, координирующий агрегаты Courier и Order.
 * <p>
 * Агрегаты не должны изменять состояние друг друга, поэтому сценарии «распределить заказ на курьера», «назначить заказ
 * курьеру» и «завершить доставку заказа», затрагивающие две границы консистентности, реализованы здесь. Каждый агрегат
 * при этом инкапсулирует только свои инварианты:
 * <ul>
 * <li>{@link Order} — допустимые переходы статусов;</li>
 * <li>{@link Courier} — ограничение по объему и наличие назначения.</li>
 * </ul>
 */
public interface CourierOrderService {

    /**
     * Диспетчеризация: распределяет один заказ в статусе CREATED на самого подходящего курьера из списка.
     * <p>
     * Бизнес-правила:
     * <ul>
     * <li>за 1 раз диспетчеризуется только 1 заказ;</li>
     * <li>курьеры, у которых недостаточно свободного объема (переполнены), не участвуют;</li>
     * <li>расстояние считается от текущего местоположения курьера до заказа (манхэттенская метрика);</li>
     * <li>побеждает курьер, который ближе всего к заказу;</li>
     * <li>победитель берет заказ: заказ переводится в ASSIGNED, у курьера создается Assignment;</li>
     * <li>если курьеров нет или все переполнены — диспетчеризация не состоялась, бизнес-ошибка.</li>
     * </ul>
     *
     * @return курьер-победитель или бизнес-ошибка
     */
    Result<Courier, Error> dispatch(Order order, List<Courier> couriers);

    /**
     * Назначить заказ курьеру: переводит заказ в ASSIGNED и создает у курьера Assignment.
     */
    UnitResult<Error> assignOrder(Courier courier, Order order);

    /**
     * Завершить доставку заказа: проверяет, что заказ назначен этому курьеру и курьер находится в 1 клетке от заказа
     * или ближе, затем завершает заказ и снимает назначение.
     */
    UnitResult<Error> completeOrder(Courier courier, Order order);
}
