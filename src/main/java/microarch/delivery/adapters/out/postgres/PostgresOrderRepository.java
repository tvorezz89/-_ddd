package microarch.delivery.adapters.out.postgres;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PostgresOrderRepository implements OrderRepository {
    private final OrderJpaRepository _orders;

    public PostgresOrderRepository(OrderJpaRepository orders) {
        _orders = orders;
    }

    @Override
    public void add(Order order) {
        _orders.save(OrderJpaEntity.fromDomain(order));
    }

    @Override
    public void update(Order order) {
        _orders.save(OrderJpaEntity.fromDomain(order));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getById(UUID id) {
        return _orders.findById(id).map(OrderJpaEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getAnyCreated() {
        return _orders.findFirstByStatus(OrderStatus.CREATED).map(OrderJpaEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAssigned() {
        return _orders.findAllByStatus(OrderStatus.ASSIGNED).stream()
                .map(OrderJpaEntity::toDomain)
                .toList();
    }
}
