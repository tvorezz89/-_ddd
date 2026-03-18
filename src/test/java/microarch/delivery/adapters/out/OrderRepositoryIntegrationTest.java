package microarch.delivery.adapters.out;

import microarch.delivery.adapters.out.postgres.OrderJpaRepository;
import microarch.delivery.core.domain.kernel.Location;
import microarch.delivery.core.domain.kernel.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderRepositoryIntegrationTest extends BasePostgresContainerTest {

    @Autowired
    OrderRepository repository;

    @Autowired
    OrderJpaRepository orderJpaRepository;

    @BeforeEach
    void clear() {
        orderJpaRepository.deleteAll();
    }

    @Test
    void add_shouldWorkProperly() {
        var id = UUID.randomUUID();
        var location = Location.create(5, 5).getValue();
        var volume = Volume.create(10).getValue();
        var order = Order.create(id, location, volume).getValue();
        repository.add(order);

        var foundOrderOpt = repository.findById(id);

        assertThat(foundOrderOpt.isPresent()).isTrue();
        assertThat(foundOrderOpt.get().equals(order)).isTrue();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void update_shouldWorkProperly() {
        var order = createOrder();
        repository.add(order);

        var courierId = UUID.randomUUID();
        order.manageToCourier(courierId);

        repository.update(order);

        var result = repository.findById(order.getId()).get();

        assertThat(result.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
        assertThat(result.getCourierId()).isEqualTo(courierId);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void findAnyByStatusCreated_shouldWorkProperly() {
        var order1 = createOrder();
        var order2 = createOrder();
        repository.add(order1);
        repository.add(order2);

        var courierId = UUID.randomUUID();
        order2.manageToCourier(courierId);
        repository.update(order2);

        var result = repository.findAnyByStatusCreated().get();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void findAllByStatusAssigned_shouldWorkProperly() {
        var order1 = createOrder();
        var order2 = createOrder();
        var order3 = createOrder();
        repository.add(order1);
        repository.add(order2);

        var courierId2 = UUID.randomUUID();
        order2.manageToCourier(courierId2);
        repository.update(order2);

        var courierId3 = UUID.randomUUID();
        order3.manageToCourier(courierId3);
        repository.update(order3);

        var result = repository.findAllByStatusAssigned();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).containsExactlyInAnyOrder(order2, order3);
    }

    private Order createOrder() {
        var id = UUID.randomUUID();
        var location = Location.create(5, 5).getValue();
        var volume = Volume.create(10).getValue();
        return Order.create(id, location, volume).getValue();
    }
}
