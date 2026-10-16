package microarch.delivery.core.domain.model.order;

/**
 * OrderStatus — статус заказа.
 * <ul>
 * <li>CREATED — заказ создан, но еще не назначен курьеру;</li>
 * <li>ASSIGNED — заказ назначен курьеру;</li>
 * <li>COMPLETED — заказ доставлен (завершен).</li>
 * </ul>
 */
public enum OrderStatus {
    CREATED, ASSIGNED, COMPLETED
}
