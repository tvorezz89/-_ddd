package microarch.delivery.core.application;

import libs.ddd.Aggregate;
import libs.ddd.DomainEventPublisher;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.domain.services.OrderDispatcherDomainServiceImpl;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.GeoClient;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DeliveryUseCasesTest {

    @Test
    void createOrderShouldSaveNewOrder() {
        var orderRepository = new InMemoryOrderRepository();
        var geoClient = new StubGeoClient(Location.mustCreate(4, 5));
        var handler = new CreateOrderCommandHandlerImpl(orderRepository, geoClient);
        var orderId = UUID.randomUUID();

        var command = CreateOrderCommand.create(orderId, "Russia", "Moscow", "Tverskaya", "1", "2", 3).getValue();
        var result = handler.handle(command);

        assertThat(result.isSuccess()).isTrue();
        assertThat(orderRepository.added).hasSize(1);
        var order = orderRepository.added.getFirst();
        assertThat(order.getId()).isEqualTo(orderId);
        assertThat(order.getLocation()).isEqualTo(Location.mustCreate(4, 5));
        assertThat(order.getVolume()).isEqualTo(Volume.mustCreate(3));
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(geoClient.street).isEqualTo("Tverskaya");
    }

    @Test
    void assignOrderShouldDispatchFirstCreatedOrderToBestCourierAndSaveBothAggregates() {
        var orderRepository = new InMemoryOrderRepository();
        var courierRepository = new InMemoryCourierRepository();
        var domainEventPublisher = new RecordingDomainEventPublisher();
        var handler = new AssignOrderCommandHandlerImpl(orderRepository, courierRepository,
                new OrderDispatcherDomainServiceImpl(), domainEventPublisher);
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(2, 2), Volume.mustCreate(3));
        var courier = Courier.mustCreate("Ivan", Location.mustCreate(2, 3));
        orderRepository.created = order;
        courierRepository.couriers = List.of(courier);

        var result = handler.handle(AssignOrderCommand.create().getValue());

        assertThat(result.isSuccess()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
        assertThat(courier.getAssignments()).hasSize(1);
        assertThat(orderRepository.updated).containsExactly(order);
        assertThat(courierRepository.updated).containsExactly(courier);
        assertThat(domainEventPublisher.published).containsExactly(order);
    }

    @Test
    void completeOrderShouldCompleteCourierAssignmentAndPublishChangedOrder() {
        var orderRepository = new InMemoryOrderRepository();
        var courierRepository = new InMemoryCourierRepository();
        var domainEventPublisher = new RecordingDomainEventPublisher();
        var handler = new CompleteOrderCommandHandlerImpl(courierRepository, orderRepository, domainEventPublisher);
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(2, 2), Volume.mustCreate(3));
        var courier = Courier.mustCreate("Ivan", Location.mustCreate(2, 3));
        courier.assign(order.getId(), order.getVolume(), order.getLocation()).getOrElseThrow();
        order.assign().getOrElseThrow();
        order.clearDomainEvents();
        orderRepository.orderById = order;
        courierRepository.courierById = courier;

        var command = CompleteOrderCommand.create(courier.getId(), order.getId()).getValue();
        var result = handler.handle(command);

        assertThat(result.isSuccess()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(orderRepository.updated).containsExactly(order);
        assertThat(courierRepository.updated).containsExactly(courier);
        assertThat(domainEventPublisher.published).containsExactly(order);
    }

    private static final class InMemoryOrderRepository implements OrderRepository {
        private final List<Order> added = new ArrayList<>();
        private final List<Order> updated = new ArrayList<>();
        private Order created;
        private Order orderById;

        @Override
        public void add(Order order) {
            added.add(order);
        }

        @Override
        public void update(Order order) {
            updated.add(order);
        }

        @Override
        public Optional<Order> findById(UUID orderId) {
            return Optional.ofNullable(orderById).filter(order -> order.getId().equals(orderId));
        }

        @Override
        public Optional<Order> findAnyCreated() {
            return Optional.ofNullable(created);
        }

        @Override
        public List<Order> findAllAssigned() {
            return List.of();
        }

        @Override
        public List<Order> findAllNotCompleted() {
            return List.of();
        }
    }

    private static final class InMemoryCourierRepository implements CourierRepository {
        private final List<Courier> updated = new ArrayList<>();
        private List<Courier> couriers = List.of();
        private Courier courierById;

        @Override
        public void add(Courier courier) {
        }

        @Override
        public void update(Courier courier) {
            updated.add(courier);
        }

        @Override
        public Optional<Courier> findById(UUID courierId) {
            return Optional.ofNullable(courierById).filter(courier -> courier.getId().equals(courierId));
        }

        @Override
        public List<Courier> findAll() {
            return couriers;
        }
    }

    private static final class StubGeoClient implements GeoClient {
        private final Location location;
        private String street;

        private StubGeoClient(Location location) {
            this.location = location;
        }

        @Override
        public Location getLocation(String street) {
            this.street = street;
            return location;
        }
    }

    private static final class RecordingDomainEventPublisher implements DomainEventPublisher {
        private final List<Aggregate<?>> published = new ArrayList<>();

        @Override
        public void publish(Iterable<? extends Aggregate<?>> aggregates) {
            aggregates.forEach(published::add);
        }
    }
}
