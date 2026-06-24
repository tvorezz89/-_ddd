package microarch.delivery.core.application;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.domain.services.OrderDispatcherDomainServiceImpl;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DispatchOrderUseCaseTest {

    @Test
    void shouldDispatchOrderAndSaveChangedAggregates() {
        var orderRepository = new InMemoryOrderRepository();
        var courierRepository = new InMemoryCourierRepository();
        var useCase = new DispatchOrderUseCase(orderRepository, courierRepository,
                new OrderDispatcherDomainServiceImpl());
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(2, 2), Volume.mustCreate(3));
        var courier = Courier.mustCreate("Ivan", Location.mustCreate(2, 3));

        useCase.dispatch(order, List.of(courier));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
        assertThat(courier.getAssignments()).hasSize(1);
        assertThat(orderRepository.updated).containsExactly(order);
        assertThat(courierRepository.updated).containsExactly(courier);
    }

    @Test
    void shouldWrapDispatchInSpringTransaction() throws NoSuchMethodException {
        var method = DispatchOrderUseCase.class.getDeclaredMethod("dispatch", Order.class, List.class);

        assertThat(method.isAnnotationPresent(Transactional.class)).isTrue();
    }

    private static final class InMemoryOrderRepository implements OrderRepository {
        private final List<Order> updated = new ArrayList<>();

        @Override
        public void add(Order order) {
        }

        @Override
        public void update(Order order) {
            updated.add(order);
        }

        @Override
        public Optional<Order> findById(UUID orderId) {
            return Optional.empty();
        }

        @Override
        public Optional<Order> findAnyCreated() {
            return Optional.empty();
        }

        @Override
        public List<Order> findAllAssigned() {
            return List.of();
        }
    }

    private static final class InMemoryCourierRepository implements CourierRepository {
        private final List<Courier> updated = new ArrayList<>();

        @Override
        public void add(Courier courier) {
        }

        @Override
        public void update(Courier courier) {
            updated.add(courier);
        }

        @Override
        public Optional<Courier> findById(UUID courierId) {
            return Optional.empty();
        }

        @Override
        public List<Courier> findAll() {
            return List.of();
        }
    }
}
