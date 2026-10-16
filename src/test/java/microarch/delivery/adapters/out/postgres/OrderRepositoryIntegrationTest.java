package microarch.delivery.adapters.out.postgres;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderRepositoryIntegrationTest extends PostgresIntegrationTestBase {

    @Autowired
    OrderRepository repository;

    @Autowired
    OrderJpaRepository jpa;

    @BeforeEach
    void cleanUp() {
        jpa.deleteAll();
    }

    @Test
    void canAddAndGetOrderById() {
        // Arrange
        var orderId = UUID.randomUUID();
        var order = Order.mustCreate(orderId, Location.mustCreate(5, 5), Volume.mustCreate(10));

        // Act
        repository.add(order);
        var loaded = repository.findById(orderId);

        // Assert
        assertThat(loaded).isPresent();
        var restored = loaded.get();
        assertThat(restored.getId()).isEqualTo(orderId);
        assertThat(restored.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(restored.getLocation()).isEqualTo(Location.mustCreate(5, 5));
        assertThat(restored.getVolume()).isEqualTo(Volume.mustCreate(10));
    }

    @Test
    void canUpdateOrderStatus() {
        // Arrange
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(1, 2), Volume.mustCreate(5));
        repository.add(order);
        order.assign();

        // Act
        repository.update(order);
        var loaded = repository.findById(order.getId());

        // Assert
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getStatus()).isEqualTo(OrderStatus.ASSIGNED);
    }

    @Test
    void canGetFirstOrderInCreatedStatus() {
        // Arrange
        var created = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(2, 2), Volume.mustCreate(5));
        var assigned = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(3, 3), Volume.mustCreate(5));
        assigned.assign();
        repository.add(created);
        repository.add(assigned);

        // Act
        var loaded = repository.getFirstOrderInStatus(OrderStatus.CREATED);

        // Assert
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(loaded.get().getId()).isEqualTo(created.getId());
    }

    @Test
    void returnsEmptyWhenNoCreatedOrders() {
        // Act
        var loaded = repository.getFirstOrderInStatus(OrderStatus.CREATED);

        // Assert
        assertThat(loaded).isEmpty();
    }

    @Test
    void canGetAllAssignedOrders() {
        // Arrange
        var assigned1 = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(1, 1), Volume.mustCreate(5));
        var assigned2 = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(2, 2), Volume.mustCreate(5));
        var created = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(3, 3), Volume.mustCreate(5));
        assigned1.assign();
        assigned2.assign();
        repository.add(assigned1);
        repository.add(assigned2);
        repository.add(created);

        // Act
        List<Order> loaded = repository.getAllOrdersInStatus(OrderStatus.ASSIGNED);

        // Assert
        assertThat(loaded).hasSize(2);
        assertThat(loaded).allMatch(o -> o.getStatus() == OrderStatus.ASSIGNED);
        assertThat(loaded).extracting(Order::getId).containsExactlyInAnyOrder(assigned1.getId(), assigned2.getId());
    }
}
