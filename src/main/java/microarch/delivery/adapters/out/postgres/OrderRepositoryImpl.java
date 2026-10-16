package microarch.delivery.adapters.out.postgres;

import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository jpa;

    public OrderRepositoryImpl(OrderJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Order add(Order order) {
        return jpa.save(order);
    }

    @Override
    public void update(Order order) {
        jpa.save(order);
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return jpa.findById(orderId);
    }

    @Override
    public Optional<Order> getFirstOrderInStatus(OrderStatus status) {
        return jpa.findFirstByStatus(status);
    }

    @Override
    public List<Order> getAllOrdersInStatus(OrderStatus status) {
        return jpa.findByStatus(status);
    }
}
