package microarch.delivery.core.domain.model.order;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Order Aggregate")
class OrderTest {

    private static final UUID ORDER_ID = UUID.randomUUID();
    private static final Location LOCATION = Location.mustCreate(5, 5);
    private static final Volume VOLUME = Volume.mustCreate(10);

    @Nested
    @DisplayName("create() — success cases")
    class CreateSuccess {

        @Test
        @DisplayName("should return success result for valid arguments")
        void shouldReturnSuccessForValidArgs() {
            var result = Order.create(ORDER_ID, LOCATION, VOLUME);

            assertThat(result.isSuccess()).isTrue();
        }

        @Test
        @DisplayName("should set id from argument")
        void shouldSetId() {
            var order = Order.create(ORDER_ID, LOCATION, VOLUME).getValue();

            assertThat(order.getId()).isEqualTo(ORDER_ID);
        }

        @Test
        @DisplayName("should set location from argument")
        void shouldSetLocation() {
            var order = Order.create(ORDER_ID, LOCATION, VOLUME).getValue();

            assertThat(order.getLocation()).isEqualTo(LOCATION);
        }

        @Test
        @DisplayName("should set volume from argument")
        void shouldSetVolume() {
            var order = Order.create(ORDER_ID, LOCATION, VOLUME).getValue();

            assertThat(order.getVolume()).isEqualTo(VOLUME);
        }

        @Test
        @DisplayName("should set initial status to CREATED")
        void shouldSetInitialStatusToCreated() {
            var order = Order.create(ORDER_ID, LOCATION, VOLUME).getValue();

            assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        }

        @Test
        @DisplayName("should allow null volume")
        void shouldAllowNullVolume() {
            var result = Order.create(ORDER_ID, LOCATION, null);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getValue().getVolume()).isNull();
        }
    }

    @Nested
    @DisplayName("create() — failure cases")
    class CreateFailure {

        @Test
        @DisplayName("should throw NullPointerException when id is null")
        void shouldThrowWhenIdNull() {
            assertThatThrownBy(() -> Order.create(null, LOCATION, VOLUME))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("should throw NullPointerException when location is null")
        void shouldThrowWhenLocationNull() {
            assertThatThrownBy(() -> Order.create(ORDER_ID, null, VOLUME))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("location");
        }
    }

    @Nested
    @DisplayName("changeStatus() — valid transitions")
    class ChangeStatusValid {

        @Test
        @DisplayName("CREATED -> ASSIGNED should succeed and update status")
        void createdToAssignedShouldSucceed() {
            var order = Order.mustCreate(ORDER_ID, LOCATION, VOLUME);

            var result = order.changeStatus(OrderStatus.ASSIGNED);

            assertThat(result.isSuccess()).isTrue();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
        }

        @Test
        @DisplayName("ASSIGNED -> COMPLETED should succeed and update status")
        void assignedToCompletedShouldSucceed() {
            var order = Order.mustCreate(ORDER_ID, LOCATION, VOLUME);
            order.changeStatus(OrderStatus.ASSIGNED);

            var result = order.changeStatus(OrderStatus.COMPLETED);

            assertThat(result.isSuccess()).isTrue();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        @DisplayName("should transition through full lifecycle: CREATED -> ASSIGNED -> COMPLETED")
        void shouldTransitionThroughFullLifecycle() {
            var order = Order.mustCreate(ORDER_ID, LOCATION, VOLUME);

            order.changeStatus(OrderStatus.ASSIGNED);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.ASSIGNED);

            order.changeStatus(OrderStatus.COMPLETED);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }
    }

    @Nested
    @DisplayName("changeStatus() — invalid transitions")
    class ChangeStatusInvalid {

        @Test
        @DisplayName("CREATED -> COMPLETED should not change status (skip step)")
        void createdToCompletedShouldNotChangeStatus() {
            var order = Order.mustCreate(ORDER_ID, LOCATION, VOLUME);

            order.changeStatus(OrderStatus.COMPLETED);

            assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        }

        @Test
        @DisplayName("CREATED -> CREATED should not change status (self-transition)")
        void createdToCreatedShouldNotChangeStatus() {
            var order = Order.mustCreate(ORDER_ID, LOCATION, VOLUME);

            order.changeStatus(OrderStatus.CREATED);

            assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        }

        @Test
        @DisplayName("ASSIGNED -> CREATED should not change status (backward)")
        void assignedToCreatedShouldNotChangeStatus() {
            var order = Order.mustCreate(ORDER_ID, LOCATION, VOLUME);
            order.changeStatus(OrderStatus.ASSIGNED);

            order.changeStatus(OrderStatus.CREATED);

            assertThat(order.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
        }

        @Test
        @DisplayName("COMPLETED should reject all transitions (terminal state)")
        void completedShouldRejectAllTransitions() {
            var order = Order.mustCreate(ORDER_ID, LOCATION, VOLUME);
            order.changeStatus(OrderStatus.ASSIGNED);
            order.changeStatus(OrderStatus.COMPLETED);

            for (OrderStatus target : OrderStatus.values()) {
                order.changeStatus(target);

                assertThat(order.getStatus())
                        .as("COMPLETED -> %s should not change status", target)
                        .isEqualTo(OrderStatus.COMPLETED);
            }
        }

        @Test
        @DisplayName("should throw NullPointerException when newStatus is null")
        void shouldThrowWhenNewStatusNull() {
            var order = Order.mustCreate(ORDER_ID, LOCATION, VOLUME);

            assertThatThrownBy(() -> order.changeStatus(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("newStatus");
        }
    }

    @Nested
    @DisplayName("changeStatus() — result value")
    class ChangeStatusResult {

        @Test
        @DisplayName("should return success for valid transition")
        void shouldReturnSuccessForValidTransition() {
            var order = Order.mustCreate(ORDER_ID, LOCATION, VOLUME);

            var result = order.changeStatus(OrderStatus.ASSIGNED);

            assertThat(result.isSuccess()).isTrue();
        }

        @Test
        @DisplayName("should return failure for invalid transition")
        void bugAlwaysReturnsSuccess() {
            var order = Order.mustCreate(ORDER_ID, LOCATION, VOLUME);

            var result = order.changeStatus(OrderStatus.COMPLETED);

            assertThat(result.isFailure()).isTrue();
            assertThat(result.getError().getCode()).isEqualTo("illegalStatusChange");
        }
    }
}