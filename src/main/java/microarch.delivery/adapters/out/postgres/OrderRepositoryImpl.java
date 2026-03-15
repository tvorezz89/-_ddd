package microarch.delivery.adapters.out.postgres;

import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    @Override
    public Order save(Order order) {
        return jpaRepository.save(order);
    }

    @Override
    public Order update(Order order) {
        return jpaRepository.save(order);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Order> findAnyCreated() {
        return jpaRepository.findFirstByStatus(OrderStatus.CREATED);
    }

    @Override
    public List<Order> findAllAssigned() {
        return jpaRepository.findAllByStatus(OrderStatus.ASSIGNED);
    }
}
