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
    public void save(Order order) {
        this.jpa.save(order);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return this.jpa.findById(id);
    }

    @Override
    public Optional<Order> getNewOrder() {
        return this.jpa.findFirstByStatus(OrderStatus.CREATED);
    }

    @Override
    public List<Order> findAllAssignedOrders() {
        return this.jpa.findAllByStatus(OrderStatus.ASSIGNED);
    }
}
