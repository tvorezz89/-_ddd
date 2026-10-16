package microarch.delivery.core.domain.services;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CourierOrderServiceTest {

    private final CourierOrderService service = new CourierOrderServiceImpl();

    private final Location startLocation = Location.mustCreate(5, 5);

    private Order createOrder(int volumeValue, Location location) {
        return Order.mustCreate(UUID.randomUUID(), location, Volume.mustCreate(volumeValue));
    }

    /* ---------- assignOrder ---------- */

    @Test
    void shouldAssignOrderToCourier() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);
        var order = createOrder(5, Location.mustCreate(1, 1));

        // Act
        var result = service.assignOrder(courier, order);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
        assertThat(courier.getAssignments()).hasSize(1);
        assertThat(courier.getAssignments().get(0).getOrderId()).isEqualTo(order.getId());
        assertThat(courier.getTakenVolume().getValue()).isEqualTo(5);
    }

    @Test
    void shouldReturnErrorWhenAssignOrderExceedingMaxVolume() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);
        service.assignOrder(courier, createOrder(15, Location.mustCreate(1, 1)));
        var order = createOrder(10, Location.mustCreate(2, 2));

        // Act
        var result = service.assignOrder(courier, order);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(courier.getAssignments()).hasSize(1);
    }

    @Test
    void shouldReturnErrorWhenAssignAlreadyAssignedOrder() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);
        var other = Courier.mustCreate("Пётр", startLocation);
        var order = createOrder(5, Location.mustCreate(1, 1));
        service.assignOrder(other, order);

        // Act
        var result = service.assignOrder(courier, order);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(courier.getAssignments()).isEmpty();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
    }

    @Test
    void shouldReturnErrorWhenAssignOrderWithNullCourier() {
        // Act
        var result = service.assignOrder(null, createOrder(5, startLocation));

        // Assert
        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void shouldReturnErrorWhenAssignNullOrder() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);

        // Act
        var result = service.assignOrder(courier, null);

        // Assert
        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void shouldBeAbleToAssignOrdersUpToExactlyMaxVolume() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);

        // Act
        var r1 = service.assignOrder(courier, createOrder(10, Location.mustCreate(1, 1)));
        var r2 = service.assignOrder(courier, createOrder(10, Location.mustCreate(2, 2)));

        // Assert
        assertThat(r1.isSuccess()).isTrue();
        assertThat(r2.isSuccess()).isTrue();
        assertThat(courier.getTakenVolume().getValue()).isEqualTo(20);
    }

    /* ---------- completeOrder ---------- */

    @Test
    void shouldCompleteOrderWhenCourierIsOneStepAway() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);
        var order = createOrder(5, Location.mustCreate(6, 5));
        service.assignOrder(courier, order);
        courier.move(Location.mustCreate(6, 5));

        // Act
        var result = service.completeOrder(courier, order);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(courier.getAssignments()).isEmpty();
        assertThat(courier.getTakenVolume().getValue()).isEqualTo(0);
    }

    @Test
    void shouldCompleteOrderWhenCourierIsAtOrderLocation() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);
        var order = createOrder(5, startLocation);
        service.assignOrder(courier, order);

        // Act
        var result = service.completeOrder(courier, order);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void shouldReturnErrorWhenCompleteOrderFromTooFar() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);
        var order = createOrder(5, Location.mustCreate(10, 10));
        service.assignOrder(courier, order);

        // Act
        var result = service.completeOrder(courier, order);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
        assertThat(courier.getAssignments()).hasSize(1);
    }

    @Test
    void shouldReturnErrorWhenCompleteOrderWithoutAssignment() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);
        var order = createOrder(5, startLocation);

        // Act
        var result = service.completeOrder(courier, order);

        // Assert
        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void shouldReturnErrorWhenCompleteOrderWithNullCourier() {
        // Act
        var result = service.completeOrder(null, createOrder(5, startLocation));

        // Assert
        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void shouldReturnErrorWhenCompleteNullOrder() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);

        // Act
        var result = service.completeOrder(courier, null);

        // Assert
        assertThat(result.isFailure()).isTrue();
    }
}
