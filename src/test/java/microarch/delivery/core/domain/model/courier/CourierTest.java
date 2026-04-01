package microarch.delivery.core.domain.model.courier;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Assignment;
import microarch.delivery.core.domain.model.order.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Courier Aggregate")
class CourierTest {

    private static final String NAME = "Ivan";
    private static final Location LOCATION = Location.mustCreate(5, 5);

    @Nested
    @DisplayName("create() — success cases")
    class CreateSuccess {

        @Test
        @DisplayName("should return success result for valid arguments")
        void shouldReturnSuccessForValidArgs() {
            var result = Courier.create(NAME, LOCATION);

            assertThat(result.isSuccess()).isTrue();
        }

        @Test
        @DisplayName("should set name from argument")
        void shouldSetName() {
            var courier = Courier.create(NAME, LOCATION).getValue();

            assertThat(courier.getName()).isEqualTo(NAME);
        }

        @Test
        @DisplayName("should set location from argument")
        void shouldSetLocation() {
            var courier = Courier.create(NAME, LOCATION).getValue();

            assertThat(courier.getLocation()).isEqualTo(LOCATION);
        }

        @Test
        @DisplayName("should set default max volume of 20")
        void shouldSetDefaultMaxVolume() {
            var courier = Courier.create(NAME, LOCATION).getValue();

            assertThat(courier.getMaxVolume()).isEqualTo(Volume.mustCreate(20));
        }

        @Test
        @DisplayName("should generate a non-null id")
        void shouldGenerateNonNullId() {
            var courier = Courier.create(NAME, LOCATION).getValue();

            assertThat(courier.getId()).isNotNull();
        }

        @Test
        @DisplayName("should generate unique id for each courier")
        void shouldGenerateUniqueId() {
            var a = Courier.create(NAME, LOCATION).getValue();
            var b = Courier.create(NAME, LOCATION).getValue();

            assertThat(a.getId()).isNotEqualTo(b.getId());
        }

        @Test
        @DisplayName("should start with empty assignments list")
        void shouldStartWithEmptyAssignments() {
            var courier = Courier.create(NAME, LOCATION).getValue();

            assertThat(courier.getAssignments()).isEmpty();
        }
    }

    @Nested
    @DisplayName("create() — failure cases")
    class CreateFailure {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("should fail when name is null or empty")
        void shouldFailWhenNameNullOrEmpty(String name) {
            var result = Courier.create(name, LOCATION);

            assertThat(result.isSuccess()).isFalse();
        }

        @Test
        @DisplayName("should fail when name is blank (whitespace only)")
        void shouldFailWhenNameBlank() {
            var result = Courier.create("   ", LOCATION);

            assertThat(result.isSuccess()).isFalse();
        }

        @Test
        @DisplayName("should throw NullPointerException when location is null")
        void shouldThrowWhenLocationNull() {
            assertThatThrownBy(() -> Courier.create(NAME, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("takeOrder() — success cases")
    class TakeOrderSuccess {

        @Test
        @DisplayName("should succeed when order volume fits within max volume")
        void shouldSucceedWhenVolumeFits() {
            var courier = Courier.mustCreate(NAME, LOCATION);
            var order = Order.mustCreate(UUID.randomUUID(), LOCATION, Volume.mustCreate(10));

            var result = courier.takeOrder(order);

            assertThat(result.isSuccess()).isTrue();
        }

        @Test
        @DisplayName("should add assignment to assignments list")
        void shouldAddAssignment() {
            var courier = Courier.mustCreate(NAME, LOCATION);
            var order = Order.mustCreate(UUID.randomUUID(), LOCATION, Volume.mustCreate(10));

            courier.takeOrder(order);

            assertThat(courier.getAssignments()).hasSize(1);
        }

        @Test
        @DisplayName("assignment should reference the order's id, volume and location")
        void assignmentShouldReferenceOrderData() {
            var courier = Courier.mustCreate(NAME, LOCATION);
            var orderId = UUID.randomUUID();
            var orderVolume = Volume.mustCreate(5);
            var orderLocation = Location.mustCreate(3, 7);
            var order = Order.mustCreate(orderId, orderLocation, orderVolume);

            courier.takeOrder(order);

            var assignment = courier.getAssignments().getFirst();
            assertThat(assignment.getOrderId()).isEqualTo(orderId);
            assertThat(assignment.getVolume()).isEqualTo(orderVolume);
            assertThat(assignment.getLocation()).isEqualTo(orderLocation);
        }

        @Test
        @DisplayName("should accept order that fills volume exactly to max (20)")
        void shouldAcceptOrderFillingExactlyToMax() {
            var courier = Courier.mustCreate(NAME, LOCATION);
            var order = Order.mustCreate(UUID.randomUUID(), LOCATION, Volume.mustCreate(20));

            var result = courier.takeOrder(order);

            assertThat(result.isSuccess()).isTrue();
        }

        @Test
        @DisplayName("should accept multiple orders while total volume is within max")
        void shouldAcceptMultipleOrders() {
            var courier = Courier.mustCreate(NAME, LOCATION);
            var order1 = Order.mustCreate(UUID.randomUUID(), LOCATION, Volume.mustCreate(7));
            var order2 = Order.mustCreate(UUID.randomUUID(), LOCATION, Volume.mustCreate(7));
            var order3 = Order.mustCreate(UUID.randomUUID(), LOCATION, Volume.mustCreate(6));

            courier.takeOrder(order1);
            courier.takeOrder(order2);
            var result = courier.takeOrder(order3);

            assertThat(result.isSuccess()).isTrue();
            assertThat(courier.getAssignments()).hasSize(3);
        }
    }

    @Nested
    @DisplayName("takeOrder() — failure cases")
    class TakeOrderFailure {

        @Test
        @DisplayName("should fail when order volume exceeds max volume")
        void shouldFailWhenVolumeExceedsMax() {
            var courier = Courier.mustCreate(NAME, LOCATION);
            var order = Order.mustCreate(UUID.randomUUID(), LOCATION, Volume.mustCreate(21));

            var result = courier.takeOrder(order);

            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getError().getCode()).isEqualTo("orderToHeavyForCourier");
        }

        @Test
        @DisplayName("should fail when adding order would exceed remaining capacity")
        void shouldFailWhenExceedingRemainingCapacity() {
            var courier = Courier.mustCreate(NAME, LOCATION);
            courier.takeOrder(Order.mustCreate(UUID.randomUUID(), LOCATION, Volume.mustCreate(15)));

            var order = Order.mustCreate(UUID.randomUUID(), LOCATION, Volume.mustCreate(6));

            var result = courier.takeOrder(order);

            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getError().getCode()).isEqualTo("orderToHeavyForCourier");
        }

        @Test
        @DisplayName("should not add assignment when volume exceeds max")
        void shouldNotAddAssignmentWhenOverCapacity() {
            var courier = Courier.mustCreate(NAME, LOCATION);
            courier.takeOrder(Order.mustCreate(UUID.randomUUID(), LOCATION, Volume.mustCreate(15)));

            courier.takeOrder(Order.mustCreate(UUID.randomUUID(), LOCATION, Volume.mustCreate(6)));

            assertThat(courier.getAssignments()).hasSize(1);
        }

        @Test
        @DisplayName("should throw NullPointerException when order is null")
        void shouldThrowWhenOrderNull() {
            var courier = Courier.mustCreate(NAME, LOCATION);

            assertThatThrownBy(() -> courier.takeOrder(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("completeAssignment() — success cases")
    class CompleteAssignmentSuccess {

        @Test
        @DisplayName("should succeed when assignment belongs to courier and location is in reach")
        void shouldSucceedWhenAssignmentBelongsAndLocationInReach() {
            var courierLocation = Location.mustCreate(5, 5);
            var courier = Courier.mustCreate(NAME, courierLocation);
            var order = Order.mustCreate(UUID.randomUUID(), courierLocation, Volume.mustCreate(5));
            courier.takeOrder(order);
            var assignment = courier.getAssignments().getFirst();

            var result = courier.completeAssignment(assignment);

            assertThat(result.isSuccess()).isTrue();
        }

        @Test
        @DisplayName("should remove assignment from list after completion")
        void shouldRemoveAssignmentAfterCompletion() {
            var courierLocation = Location.mustCreate(5, 5);
            var courier = Courier.mustCreate(NAME, courierLocation);
            var order = Order.mustCreate(UUID.randomUUID(), courierLocation, Volume.mustCreate(5));
            courier.takeOrder(order);
            var assignment = courier.getAssignments().getFirst();

            courier.completeAssignment(assignment);

            assertThat(courier.getAssignments()).isEmpty();
        }

        @Test
        @DisplayName("should succeed when courier is 1 step away from assignment location (within reach)")
        void shouldSucceedWhenCourierOneStepAway() {
            var courier = Courier.mustCreate(NAME, Location.mustCreate(5, 5));
            var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(5, 6), Volume.mustCreate(5));
            courier.takeOrder(order);
            var assignment = courier.getAssignments().getFirst();

            var result = courier.completeAssignment(assignment);

            assertThat(result.isSuccess()).isTrue();
        }

        @Test
        @DisplayName("should only remove completed assignment, keeping others")
        void shouldOnlyRemoveCompletedAssignment() {
            var courierLocation = Location.mustCreate(5, 5);
            var courier = Courier.mustCreate(NAME, courierLocation);
            var order1 = Order.mustCreate(UUID.randomUUID(), courierLocation, Volume.mustCreate(5));
            var order2 = Order.mustCreate(UUID.randomUUID(), courierLocation, Volume.mustCreate(5));
            courier.takeOrder(order1);
            courier.takeOrder(order2);
            var firstAssignment = courier.getAssignments().getFirst();

            courier.completeAssignment(firstAssignment);

            assertThat(courier.getAssignments()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("completeAssignment() — failure cases")
    class CompleteAssignmentFailure {

        @Test
        @DisplayName("should fail when assignment does not belong to courier")
        void shouldFailWhenAssignmentNotBelongsToCourier() {
            var courier = Courier.mustCreate(NAME, LOCATION);
            var foreignAssignment = Assignment.mustCreate(UUID.randomUUID(), Volume.mustCreate(5), LOCATION);

            var result = courier.completeAssignment(foreignAssignment);

            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getError().getCode()).isEqualTo("assignmentNotTakenByCourier");
        }

        @Test
        @DisplayName("should fail when courier is too far from assignment location")
        void shouldFailWhenCourierTooFar() {
            var courier = Courier.mustCreate(NAME, Location.mustCreate(1, 1));
            var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(5, 5), Volume.mustCreate(5));
            courier.takeOrder(order);
            var assignment = courier.getAssignments().getFirst();

            var result = courier.completeAssignment(assignment);

            assertThat(result.isSuccess()).isFalse();
        }

        @Test
        @DisplayName("should not remove assignment when courier is too far")
        void shouldNotRemoveAssignmentWhenTooFar() {
            var courier = Courier.mustCreate(NAME, Location.mustCreate(1, 1));
            var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(5, 5), Volume.mustCreate(5));
            courier.takeOrder(order);
            var assignment = courier.getAssignments().getFirst();

            courier.completeAssignment(assignment);

            assertThat(courier.getAssignments()).hasSize(1);
        }

        @Test
        @DisplayName("should throw NullPointerException when assignment is null")
        void shouldThrowWhenAssignmentNull() {
            var courier = Courier.mustCreate(NAME, LOCATION);

            assertThatThrownBy(() -> courier.completeAssignment(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("changeLocation() — success cases")
    class ChangeLocationSuccess {

        @Test
        @DisplayName("should succeed when new location is 1 step away by latitude")
        void shouldSucceedWhenOneStepByLatitude() {
            var courier = Courier.mustCreate(NAME, Location.mustCreate(5, 5));

            var result = courier.changeLocation(Location.mustCreate(6, 5));

            assertThat(result.isSuccess()).isTrue();
            assertThat(courier.getLocation()).isEqualTo(Location.mustCreate(6, 5));
        }

        @Test
        @DisplayName("should succeed when new location is 1 step away by longitude")
        void shouldSucceedWhenOneStepByLongitude() {
            var courier = Courier.mustCreate(NAME, Location.mustCreate(5, 5));

            var result = courier.changeLocation(Location.mustCreate(5, 6));

            assertThat(result.isSuccess()).isTrue();
            assertThat(courier.getLocation()).isEqualTo(Location.mustCreate(5, 6));
        }

        @Test
        @DisplayName("should update location after successful move")
        void shouldUpdateLocation() {
            var courier = Courier.mustCreate(NAME, Location.mustCreate(5, 5));
            var newLocation = Location.mustCreate(5, 4);

            courier.changeLocation(newLocation);

            assertThat(courier.getLocation()).isEqualTo(newLocation);
        }

        @Test
        @DisplayName("should allow sequential moves step by step")
        void shouldAllowSequentialMoves() {
            var courier = Courier.mustCreate(NAME, Location.mustCreate(5, 5));

            courier.changeLocation(Location.mustCreate(5, 6));
            courier.changeLocation(Location.mustCreate(5, 7));
            courier.changeLocation(Location.mustCreate(6, 7));

            assertThat(courier.getLocation()).isEqualTo(Location.mustCreate(6, 7));
        }
    }

    @Nested
    @DisplayName("changeLocation() — failure cases")
    class ChangeLocationFailure {

        @Test
        @DisplayName("should fail when new location is the same as current")
        void shouldFailWhenSameLocation() {
            var courier = Courier.mustCreate(NAME, Location.mustCreate(5, 5));

            var result = courier.changeLocation(Location.mustCreate(5, 5));

            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getError().getCode()).isEqualTo("illegalMovement");
        }

        @Test
        @DisplayName("should fail when new location is too far (distance = 2)")
        void shouldFailWhenTooFar() {
            var courier = Courier.mustCreate(NAME, Location.mustCreate(5, 5));

            var result = courier.changeLocation(Location.mustCreate(5, 7));

            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getError().getCode()).isEqualTo("illegalMovement");
        }

        @Test
        @DisplayName("should succeed when moving diagonally (distance = 2)")
        void shouldSucceedWhenMovingDiagonally() {
            var courier = Courier.mustCreate(NAME, Location.mustCreate(5, 5));

            var result = courier.changeLocation(Location.mustCreate(6, 6));

            assertThat(result.isSuccess()).isTrue();
        }

        @Test
        @DisplayName("should not update location on failure")
        void shouldNotUpdateLocationOnFailure() {
            var original = Location.mustCreate(5, 5);
            var courier = Courier.mustCreate(NAME, original);

            courier.changeLocation(Location.mustCreate(5, 7));

            assertThat(courier.getLocation()).isEqualTo(original);
        }

        @Test
        @DisplayName("should throw NullPointerException when new location is null")
        void shouldThrowWhenNewLocationNull() {
            var courier = Courier.mustCreate(NAME, LOCATION);

            assertThatThrownBy(() -> courier.changeLocation(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Volume capacity — integration")
    class VolumeCapacity {

        @Test
        @DisplayName("completing assignment should free volume for new orders")
        void completingAssignmentShouldFreeVolume() {
            var courierLocation = Location.mustCreate(5, 5);
            var courier = Courier.mustCreate(NAME, courierLocation);

            courier.takeOrder(Order.mustCreate(UUID.randomUUID(), courierLocation, Volume.mustCreate(15)));
            courier.takeOrder(Order.mustCreate(UUID.randomUUID(), courierLocation, Volume.mustCreate(5)));
            assertThat(courier.getAssignments()).hasSize(2);

            var largeAssignment = courier.getAssignments().getFirst();
            courier.completeAssignment(largeAssignment);

            var newOrder = Order.mustCreate(UUID.randomUUID(), courierLocation, Volume.mustCreate(12));
            var result = courier.takeOrder(newOrder);

            assertThat(result.isSuccess()).isTrue();
            assertThat(courier.getAssignments()).hasSize(2);
        }

        @Test
        @DisplayName("should track cumulative volume across multiple orders")
        void shouldTrackCumulativeVolume() {
            var courier = Courier.mustCreate(NAME, LOCATION);

            courier.takeOrder(Order.mustCreate(UUID.randomUUID(), LOCATION, Volume.mustCreate(8)));
            courier.takeOrder(Order.mustCreate(UUID.randomUUID(), LOCATION, Volume.mustCreate(8)));

            var fitsResult = courier.takeOrder(Order.mustCreate(UUID.randomUUID(), LOCATION, Volume.mustCreate(4)));
            assertThat(fitsResult.isSuccess()).isTrue();

            var overflowResult = courier.takeOrder(Order.mustCreate(UUID.randomUUID(), LOCATION, Volume.mustCreate(1)));
            assertThat(overflowResult.isSuccess()).isFalse();
        }
    }
}