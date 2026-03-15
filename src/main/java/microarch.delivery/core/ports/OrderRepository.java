package microarch.delivery.core.ports;

import microarch.delivery.core.domain.model.order.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);

    Order update(Order order);

    Optional<Order> findById(UUID id);

    Optional<Order> findAnyCreated();

    List<Order> findAllAssigned();

    List<Order> findAllUnfinished();

}
