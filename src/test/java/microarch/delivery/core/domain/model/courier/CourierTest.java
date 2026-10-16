package microarch.delivery.core.domain.model.courier;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CourierTest {

    private final Location startLocation = Location.mustCreate(5, 5);

    @Test
    void shouldBeCreatedWithCorrectParams() {
        // Act
        var result = Courier.create("Иван", startLocation);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        var courier = result.getValue();
        assertThat(courier.getId()).isNotNull();
        assertThat(courier.getName()).isEqualTo("Иван");
        assertThat(courier.getLocation()).isEqualTo(startLocation);
        assertThat(courier.getMaxVolume().getValue()).isEqualTo(20);
        assertThat(courier.getAssignments()).isEmpty();
    }

    @Test
    void shouldReturnErrorWhenNameIsEmptyOnCreated() {
        // Act
        var result = Courier.create("", startLocation);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldReturnErrorWhenLocationIsNullOnCreated() {
        // Act
        var result = Courier.create("Иван", null);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    /* ---------- canTakeOrder / addAssignment ---------- */

    @Test
    void shouldBeAbleToTakeOrderWhenVolumeFits() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);

        // Act
        var canTake = courier.canTakeOrder(Volume.mustCreate(20));

        // Assert
        assertThat(canTake).isTrue();
    }

    @Test
    void shouldNotBeAbleToTakeOrderWhenVolumeDoesNotFit() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);
        courier.addAssignment(UUID.randomUUID(), Volume.mustCreate(10));

        // Act
        var canTake = courier.canTakeOrder(Volume.mustCreate(11));

        // Assert
        assertThat(canTake).isFalse();
    }

    @Test
    void shouldNotBeAbleToTakeOrderWhenVolumeIsNull() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);

        // Act
        var canTake = courier.canTakeOrder(null);

        // Assert
        assertThat(canTake).isFalse();
    }

    @Test
    void shouldAddAssignment() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);
        var orderId = UUID.randomUUID();

        // Act
        var result = courier.addAssignment(orderId, Volume.mustCreate(5));

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.getAssignments()).hasSize(1);
        assertThat(courier.getAssignments().get(0).getOrderId()).isEqualTo(orderId);
        assertThat(courier.getTakenVolume().getValue()).isEqualTo(5);
    }

    @Test
    void shouldReturnErrorWhenAddAssignmentExceedingMaxVolume() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);
        courier.addAssignment(UUID.randomUUID(), Volume.mustCreate(15));

        // Act
        var result = courier.addAssignment(UUID.randomUUID(), Volume.mustCreate(10));

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(courier.getAssignments()).hasSize(1);
        assertThat(courier.getTakenVolume().getValue()).isEqualTo(15);
    }

    @Test
    void shouldBeAbleToTakeOrdersUpToExactlyMaxVolume() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);

        // Act
        var r1 = courier.addAssignment(UUID.randomUUID(), Volume.mustCreate(10));
        var r2 = courier.addAssignment(UUID.randomUUID(), Volume.mustCreate(10));

        // Assert
        assertThat(r1.isSuccess()).isTrue();
        assertThat(r2.isSuccess()).isTrue();
        assertThat(courier.getTakenVolume().getValue()).isEqualTo(20);
    }

    @Test
    void shouldReturnErrorWhenAddAssignmentWithNullOrderId() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);

        // Act
        var result = courier.addAssignment(null, Volume.mustCreate(5));

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(courier.getAssignments()).isEmpty();
    }

    @Test
    void shouldReturnErrorWhenAddAssignmentWithNullVolume() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);

        // Act
        var result = courier.addAssignment(UUID.randomUUID(), null);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(courier.getAssignments()).isEmpty();
    }

    /* ---------- hasAssignment / removeAssignment ---------- */

    @Test
    void hasAssignmentShouldReflectState() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);
        var orderId = UUID.randomUUID();

        // Assert
        assertThat(courier.hasAssignment(orderId)).isFalse();

        // Act
        courier.addAssignment(orderId, Volume.mustCreate(5));

        // Assert
        assertThat(courier.hasAssignment(orderId)).isTrue();
    }

    @Test
    void shouldRemoveAssignment() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);
        var orderId = UUID.randomUUID();
        courier.addAssignment(orderId, Volume.mustCreate(5));

        // Act
        var result = courier.removeAssignment(orderId);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.getAssignments()).isEmpty();
        assertThat(courier.getTakenVolume().getValue()).isEqualTo(0);
        assertThat(courier.hasAssignment(orderId)).isFalse();
    }

    @Test
    void shouldReturnErrorWhenRemoveAssignmentWithoutAssignment() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);

        // Act
        var result = courier.removeAssignment(UUID.randomUUID());

        // Assert
        assertThat(result.isFailure()).isTrue();
    }

    /* ---------- isWithinReach ---------- */

    @Test
    void shouldBeWithinReachWhenOneStepAway() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);

        // Act
        var result = courier.isWithinReach(Location.mustCreate(6, 5));

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void shouldBeWithinReachWhenAtSameLocation() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);

        // Act
        var result = courier.isWithinReach(startLocation);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void shouldNotBeWithinReachWhenTooFar() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);

        // Act
        var result = courier.isWithinReach(Location.mustCreate(8, 7));

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void shouldNotBeWithinReachWhenLocationIsNull() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);

        // Act
        var result = courier.isWithinReach(null);

        // Assert
        assertThat(result).isFalse();
    }

    /* ---------- move ---------- */

    @Test
    void shouldMoveOneStep() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);

        // Act
        var result = courier.move(Location.mustCreate(6, 5));

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.getLocation()).isEqualTo(Location.mustCreate(6, 5));
    }

    @Test
    void shouldNotMoveFartherThanOneStep() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);

        // Act
        var result = courier.move(Location.mustCreate(7, 5));

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(courier.getLocation()).isEqualTo(startLocation);
    }

    @Test
    void shouldNotMoveWhenTargetIsNull() {
        // Arrange
        var courier = Courier.mustCreate("Иван", startLocation);

        // Act
        var result = courier.move(null);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(courier.getLocation()).isEqualTo(startLocation);
    }
}
