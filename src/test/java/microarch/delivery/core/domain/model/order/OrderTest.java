package microarch.delivery.core.domain.model.order;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    private final UUID orderId = UUID.randomUUID();
    private final Location location = Location.mustCreate(5, 5);
    private final Volume volume = Volume.mustCreate(3);

    @Test
    void shouldBeCreatedWithCorrectParams() {
        // Act
        var result = Order.create(orderId, location, volume);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        var order = result.getValue();
        assertThat(order.getId()).isEqualTo(orderId);
        assertThat(order.getLocation()).isEqualTo(location);
        assertThat(order.getVolume()).isEqualTo(volume);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    void shouldReturnErrorWhenIdIsNullOnCreated() {
        // Act
        var result = Order.create(null, location, volume);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldReturnErrorWhenLocationIsNullOnCreated() {
        // Act
        var result = Order.create(orderId, null, volume);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldReturnErrorWhenVolumeIsNullOnCreated() {
        // Act
        var result = Order.create(orderId, location, null);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldAssignOrderWhenStatusIsCreated() {
        // Arrange
        var order = Order.mustCreate(orderId, location, volume);

        // Act
        var result = order.assign();

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
    }

    @Test
    void shouldReturnErrorWhenAssignOrderTwice() {
        // Arrange
        var order = Order.mustCreate(orderId, location, volume);
        order.assign();

        // Act
        var result = order.assign();

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
    }

    @Test
    void shouldReturnErrorWhenCompleteOrderWithCreatedStatus() {
        // Arrange
        var order = Order.mustCreate(orderId, location, volume);

        // Act
        var result = order.complete();

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    void shouldCompleteOrderWhenStatusIsAssigned() {
        // Arrange
        var order = Order.mustCreate(orderId, location, volume);
        order.assign();

        // Act
        var result = order.complete();

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void shouldReturnErrorWhenCompleteOrderTwice() {
        // Arrange
        var order = Order.mustCreate(orderId, location, volume);
        order.assign();
        order.complete();

        // Act
        var result = order.complete();

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 5, 20, 100 })
    void shouldBeCreatedWithDifferentVolumes(int volumeValue) {
        // Act
        var order = Order.mustCreate(orderId, location, Volume.mustCreate(volumeValue));

        // Assert
        assertThat(order.getVolume().getValue()).isEqualTo(volumeValue);
    }
}
