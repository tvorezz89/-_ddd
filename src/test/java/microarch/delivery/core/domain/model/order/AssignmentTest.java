package microarch.delivery.core.domain.model.order;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Status;
import microarch.delivery.core.domain.model.Volume;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Assignment Entity")
class AssignmentTest {

    private static final UUID ORDER_ID = UUID.randomUUID();
    private static final Volume VOLUME = Volume.mustCreate(10);
    private static final Location LOCATION = Location.mustCreate(5, 5);

    @Nested
    @DisplayName("create() — success cases")
    class CreateSuccess {

        @Test
        @DisplayName("should return success result for valid arguments")
        void shouldReturnSuccessForValidArgs() {
            var result = Assignment.create(ORDER_ID, VOLUME, LOCATION);

            assertThat(result.isSuccess()).isTrue();
            Assignment assignment = result.getValue();
            assertThat(assignment.getId()).isNotNull();
            assertThat(assignment.getOrderId()).isEqualTo(ORDER_ID);
            assertThat(assignment.getVolume()).isEqualTo(VOLUME);
            assertThat(assignment.getLocation()).isEqualTo(LOCATION);
            assertThat(assignment.getStatus()).isEqualTo(Status.ASSIGNED);
        }
    }

    @Nested
    @DisplayName("create() — failure cases")
    class CreateFailure {

        @Test
        @DisplayName("should throw NullPointerException when orderId is null")
        void shouldThrowWhenOrderIdNull() {
            assertThatThrownBy(() -> Assignment.create(null, VOLUME, LOCATION))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("orderId");
        }

        @Test
        @DisplayName("should throw NullPointerException when volume is null")
        void shouldThrowWhenVolumeNull() {
            assertThatThrownBy(() -> Assignment.create(ORDER_ID, null, LOCATION))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("volume");
        }

        @Test
        @DisplayName("should throw NullPointerException when location is null")
        void shouldThrowWhenLocationNull() {
            assertThatThrownBy(() -> Assignment.create(ORDER_ID, VOLUME, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("location");
        }
    }

    @Nested
    @DisplayName("mustCreate()")
    class MustCreate {

        @Test
        @DisplayName("should return Assignment for valid arguments")
        void shouldReturnAssignmentForValidArgs() {
            var assignment = Assignment.mustCreate(ORDER_ID, VOLUME, LOCATION);

            assertThat(assignment).isNotNull();
            assertThat(assignment.getOrderId()).isEqualTo(ORDER_ID);
        }

        @Test
        @DisplayName("should throw when orderId is null")
        void shouldThrowWhenOrderIdNull() {
            assertThatThrownBy(() -> Assignment.mustCreate(null, VOLUME, LOCATION))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("completeOrderByCourierLocation()")
    class CompleteOrder {

        @Test
        @DisplayName("should fail when courier is too far (distance = 2)")
        void shouldFailWhenCourierTooFar() {
            var assignment = Assignment.mustCreate(ORDER_ID, VOLUME, Location.mustCreate(5, 5));
            var courierLocation = Location.mustCreate(5, 7);

            var result = assignment.completeOrderByCourierLocation(courierLocation);

            assertThat(result.isSuccess()).isFalse();
            assertThat(assignment.getStatus()).isEqualTo(Status.ASSIGNED);
        }

        @Test
        @DisplayName("should succeed when courier is neighbor")
        void shouldSucceedWhenCourierDiagonal() {
            var assignment = Assignment.mustCreate(ORDER_ID, VOLUME, Location.mustCreate(5, 5));
            var courierLocation = Location.mustCreate(6, 6);

            var result = assignment.completeOrderByCourierLocation(courierLocation);

            assertThat(result.isSuccess()).isTrue();
        }

        @Test
        @DisplayName("should fail when courier location is null")
        void shouldFailWhenCourierLocationNull() {
            var assignment = Assignment.mustCreate(ORDER_ID, VOLUME, LOCATION);

            var result = assignment.completeOrderByCourierLocation(null);

            assertThat(result.isSuccess()).isFalse();
            assertThat(assignment.getStatus()).isEqualTo(Status.ASSIGNED);
        }

        @Test
        @DisplayName("should fail when assignment is already completed")
        void shouldFailWhenAlreadyCompleted() {
            var assignment = completedAssignment();
            var courierLocation = assignment.getLocation();

            var result = assignment.completeOrderByCourierLocation(courierLocation);

            assertThat(result.isSuccess()).isFalse();
            assertThat(assignment.getStatus()).isEqualTo(Status.COMPLETED);
        }
    }

    @Nested
    @DisplayName("Error messages")
    class ErrorMessages {

        @Test
        @DisplayName("should return 'location too far' error when courier is out of range")
        void shouldReturnLocationTooFarError() {
            var assignment = Assignment.mustCreate(ORDER_ID, VOLUME, Location.mustCreate(5, 5));
            var courierLocation = Location.mustCreate(5, 8);

            var result = assignment.completeOrderByCourierLocation(courierLocation);

            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getError().getCode()).isEqualTo("location.too.far.for.completion");
        }

        @Test
        @DisplayName("should return 'already completed' error when status is COMPLETED")
        void shouldReturnAlreadyCompletedError() {
            var assignment = completedAssignment();

            var result = assignment.completeOrderByCourierLocation(assignment.getLocation());

            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getError().getCode()).isEqualTo("assignment.already.completed");
        }

        @Test
        @DisplayName("should return 'value required' error when courier location is null")
        void shouldReturnValueRequiredErrorWhenNull() {
            var assignment = Assignment.mustCreate(ORDER_ID, VOLUME, LOCATION);

            var result = assignment.completeOrderByCourierLocation(null);

            assertThat(result.isSuccess()).isFalse();
        }
    }

    private static Assignment completedAssignment() {
        var assignment = Assignment.mustCreate(ORDER_ID, VOLUME, LOCATION);
        assignment.completeOrderByCourierLocation(assignment.getLocation());
        return assignment;
    }
}