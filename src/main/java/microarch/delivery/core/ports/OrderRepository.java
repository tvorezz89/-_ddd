package microarch.delivery.core.ports;

import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Порт для хранения агрегата Order.
 */
public interface OrderRepository {
    Order add(Order order);

    void update(Order order);

    Optional<Order> findById(UUID orderId);

    /**
     * Получить один любой новый заказ (в статусе CREATED).
     */
    Optional<Order> getFirstOrderInStatus(OrderStatus status);

    /**
     * Получить все назначенные заказы (в статусе ASSIGNED).
     */
    List<Order> getAllOrdersInStatus(OrderStatus status);
}
