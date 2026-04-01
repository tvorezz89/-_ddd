package microarch.delivery.core.domain.model.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OrderStatus Enum")
class OrderStatusTest {

    @Nested
    @DisplayName("Enum constants")
    class EnumConstants {

        @Test
        @DisplayName("CREATED should have id=1 and name='created'")
        void createdShouldHaveCorrectIdAndName() {
            assertThat(OrderStatus.CREATED.getId()).isEqualTo(1);
            assertThat(OrderStatus.CREATED.getName()).isEqualTo("created");
        }

        @Test
        @DisplayName("ASSIGNED should have id=2 and name='assigned'")
        void assignedShouldHaveCorrectIdAndName() {
            assertThat(OrderStatus.ASSIGNED.getId()).isEqualTo(2);
            assertThat(OrderStatus.ASSIGNED.getName()).isEqualTo("assigned");
        }

        @Test
        @DisplayName("COMPLETED should have id=3 and name='completed'")
        void completedShouldHaveCorrectIdAndName() {
            assertThat(OrderStatus.COMPLETED.getId()).isEqualTo(3);
            assertThat(OrderStatus.COMPLETED.getName()).isEqualTo("completed");
        }
    }

    @Nested
    @DisplayName("getByName() — success cases")
    class GetByNameSuccess {

        @ParameterizedTest(name = "name=''{0}''")
        @CsvSource({"created, CREATED", "assigned, ASSIGNED", "completed, COMPLETED"})
        @DisplayName("should find status by exact lowercase name")
        void shouldFindByExactName(String name, OrderStatus expected) {
            var result = OrderStatus.getByName(name);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getValue()).isEqualTo(expected);
        }

        @ParameterizedTest(name = "name=''{0}''")
        @CsvSource({"CREATED, CREATED", "ASSIGNED, ASSIGNED", "COMPLETED, COMPLETED"})
        @DisplayName("should find status by uppercase name (case-insensitive)")
        void shouldFindByUppercaseName(String name, OrderStatus expected) {
            var result = OrderStatus.getByName(name);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getValue()).isEqualTo(expected);
        }

        @ParameterizedTest(name = "name=''{0}''")
        @CsvSource({"CrEaTeD, CREATED", "AsSiGnEd, ASSIGNED", "cOmPlEtEd, COMPLETED"})
        @DisplayName("should find status by mixed-case name")
        void shouldFindByMixedCaseName(String name, OrderStatus expected) {
            var result = OrderStatus.getByName(name);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getValue()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("getByName() — failure cases")
    class GetByNameFailure {

        @ParameterizedTest(name = "name=''{0}''")
        @ValueSource(strings = {"unknown", "pending", "cancelled", "create", "complete"})
        @DisplayName("should fail for non-existent status names")
        void shouldFailForNonExistentNames(String name) {
            var result = OrderStatus.getByName(name);

            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getError().getCode()).isEqualTo("orderStatus.is.wrong");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("should fail for null and empty name")
        void shouldFailForNullAndEmpty(String name) {
            var result = OrderStatus.getByName(name);

            assertThat(result.isSuccess()).isFalse();
        }

        @Test
        @DisplayName("should fail for name with extra whitespace")
        void shouldFailForNameWithWhitespace() {
            var result = OrderStatus.getByName(" created ");

            assertThat(result.isSuccess()).isFalse();
        }
    }

    @Nested
    @DisplayName("getById() — success cases")
    class GetByIdSuccess {

        @ParameterizedTest(name = "id={0}")
        @CsvSource({"1, CREATED", "2, ASSIGNED", "3, COMPLETED"})
        @DisplayName("should find status by valid id")
        void shouldFindByValidId(int id, OrderStatus expected) {
            var result = OrderStatus.getById(id);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getValue()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("getById() — failure cases")
    class GetByIdFailure {

        @ParameterizedTest(name = "id={0}")
        @ValueSource(ints = {0, -1, 4, 100, Integer.MAX_VALUE, Integer.MIN_VALUE})
        @DisplayName("should fail for non-existent ids")
        void shouldFailForNonExistentIds(int id) {
            var result = OrderStatus.getById(id);

            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getError().getCode()).isEqualTo("orderStatus.is.wrong");
        }
    }

    @Nested
    @DisplayName("change() — valid transitions")
    class ChangeValidTransitions {

        @Test
        @DisplayName("CREATED -> ASSIGNED should succeed")
        void createdToAssignedShouldSucceed() {
            var result = OrderStatus.CREATED.change(OrderStatus.ASSIGNED);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getValue()).isEqualTo(OrderStatus.ASSIGNED);
        }

        @Test
        @DisplayName("ASSIGNED -> COMPLETED should succeed")
        void assignedToCompletedShouldSucceed() {
            var result = OrderStatus.ASSIGNED.change(OrderStatus.COMPLETED);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getValue()).isEqualTo(OrderStatus.COMPLETED);
        }
    }

    @Nested
    @DisplayName("change() — invalid transitions")
    class ChangeInvalidTransitions {

        @Test
        @DisplayName("CREATED -> CREATED should fail (self-transition)")
        void createdToCreatedShouldFail() {
            var result = OrderStatus.CREATED.change(OrderStatus.CREATED);

            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getError().getCode()).isEqualTo("orderStatus.incorrect.state");
        }

        @Test
        @DisplayName("CREATED -> COMPLETED should fail (skip step)")
        void createdToCompletedShouldFail() {
            var result = OrderStatus.CREATED.change(OrderStatus.COMPLETED);

            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getError().getCode()).isEqualTo("orderStatus.incorrect.state");
        }

        @Test
        @DisplayName("ASSIGNED -> CREATED should fail (backward)")
        void assignedToCreatedShouldFail() {
            var result = OrderStatus.ASSIGNED.change(OrderStatus.CREATED);

            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getError().getCode()).isEqualTo("orderStatus.incorrect.state");
        }

        @Test
        @DisplayName("ASSIGNED -> ASSIGNED should fail (self-transition)")
        void assignedToAssignedShouldFail() {
            var result = OrderStatus.ASSIGNED.change(OrderStatus.ASSIGNED);

            assertThat(result.isSuccess()).isFalse();
        }

        @Test
        @DisplayName("COMPLETED -> CREATED should fail (backward)")
        void completedToCreatedShouldFail() {
            var result = OrderStatus.COMPLETED.change(OrderStatus.CREATED);

            assertThat(result.isSuccess()).isFalse();
        }

        @Test
        @DisplayName("COMPLETED -> ASSIGNED should fail (backward)")
        void completedToAssignedShouldFail() {
            var result = OrderStatus.COMPLETED.change(OrderStatus.ASSIGNED);

            assertThat(result.isSuccess()).isFalse();
        }

        @Test
        @DisplayName("COMPLETED -> COMPLETED should fail (terminal state)")
        void completedToCompletedShouldFail() {
            var result = OrderStatus.COMPLETED.change(OrderStatus.COMPLETED);

            assertThat(result.isSuccess()).isFalse();
        }

        @Test
        @DisplayName("COMPLETED should have no valid transitions (terminal state)")
        void completedShouldBeTerminal() {
            for (OrderStatus target : OrderStatus.values()) {
                var result = OrderStatus.COMPLETED.change(target);

                assertThat(result.isSuccess())
                        .as("COMPLETED -> %s should fail", target)
                        .isFalse();
            }
        }
    }

    @Nested
    @DisplayName("Full lifecycle")
    class FullLifecycle {

        @Test
        @DisplayName("should transition through full lifecycle: CREATED -> ASSIGNED -> COMPLETED")
        void shouldTransitionThroughFullLifecycle() {
            var status = OrderStatus.CREATED;

            var toAssigned = status.change(OrderStatus.ASSIGNED);
            assertThat(toAssigned.isSuccess()).isTrue();

            var toCompleted = toAssigned.getValue().change(OrderStatus.COMPLETED);
            assertThat(toCompleted.isSuccess()).isTrue();
            assertThat(toCompleted.getValue()).isEqualTo(OrderStatus.COMPLETED);
        }
    }
}