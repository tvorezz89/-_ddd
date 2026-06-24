package microarch.delivery.adapters.out.postgres;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderRepositoryIntegrationTest extends PostgresIntegrationTestBase {

    @Autowired
    OrderRepository repository;

    @Test
    void shouldAddAndFindOrderById() {
        var order = newOrder(1, 2, 3);

        repository.add(order);

        var loaded = repository.findById(order.getId());
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getId()).isEqualTo(order.getId());
        assertThat(loaded.get().getLocation()).isEqualTo(order.getLocation());
        assertThat(loaded.get().getVolume()).isEqualTo(order.getVolume());
        assertThat(loaded.get().getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    void shouldUpdateOrder() {
        var order = newOrder(2, 3, 4);
        repository.add(order);
        order.assign().getOrElseThrow();

        repository.update(order);

        var loaded = repository.findById(order.getId());
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getStatus()).isEqualTo(OrderStatus.ASSIGNED);
    }

    @Test
    void shouldFindAnyCreatedOrder() {
        var assigned = newOrder(3, 4, 5);
        assigned.assign().getOrElseThrow();
        var created = newOrder(5, 6, 7);
        repository.add(assigned);
        repository.add(created);

        var loaded = repository.findAnyCreated();

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getId()).isEqualTo(created.getId());
        assertThat(loaded.get().getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    void shouldFindAllAssignedOrders() {
        var created = newOrder(1, 1, 1);
        var assigned = newOrder(2, 2, 2);
        assigned.assign().getOrElseThrow();
        repository.add(created);
        repository.add(assigned);

        var loaded = repository.findAllAssigned();

        assertThat(loaded).extracting(Order::getId).containsExactly(assigned.getId());
    }

    private static Order newOrder(int x, int y, int volume) {
        return Order.mustCreate(UUID.randomUUID(), Location.mustCreate(x, y), Volume.mustCreate(volume));
    }
}
