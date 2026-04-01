package microarch.delivery.adapters.out.postgres;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class OrderRepositoryImplTest extends PostgresIntegrationTestBase {

    @Autowired
    private OrderRepository orderRepository;

    private Order createOrder() {
        return Order.create(UUID.randomUUID(), Location.create(5, 5).getValue(), Volume.create(3).getValue())
                .getValue();
    }

    @Test
    void save_shouldPersistNewOrder() {
        Order order = createOrder();

        orderRepository.save(order);

        var found = orderRepository.findById(order.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(order.getId());
    }

    @Test
    void findById_shouldReturnEmpty_whenOrderDoesNotExist() {
        var found = orderRepository.findById(UUID.randomUUID());
        assertThat(found).isEmpty();
    }

    @Test
    void save_shouldUpdateExistingOrder() {
        Order order = createOrder();
        orderRepository.save(order);

        order.assign();
        orderRepository.save(order);

        var found = orderRepository.findById(order.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(order.getStatus());
    }

    @Test
    void getNewOrder_shouldReturnCreatedOrder() {
        Order order = createOrder();
        orderRepository.save(order);

        var newOrder = orderRepository.getNewOrder();
        assertThat(newOrder).isPresent();
    }

    @Test
    void getNewOrder_shouldNotReturnAssignedOrder() {
        Order order = createOrder();
        order.assign();
        orderRepository.save(order);

        var newOrder = orderRepository.getNewOrder();
        assertThat(newOrder).isEmpty();
    }

    @Test
    void findAllAssignedOrders_shouldReturnOnlyAssignedOrders() {
        Order created = createOrder();
        orderRepository.save(created);

        Order assigned1 = createOrder();
        assigned1.assign();
        orderRepository.save(assigned1);

        Order assigned2 = createOrder();
        assigned2.assign();
        orderRepository.save(assigned2);

        var assignedOrders = orderRepository.findAllAssignedOrders();
        assertThat(assignedOrders).hasSize(2);
        assertThat(assignedOrders).allSatisfy(o -> assertThat(o.getId()).isIn(assigned1.getId(), assigned2.getId()));
    }
}
