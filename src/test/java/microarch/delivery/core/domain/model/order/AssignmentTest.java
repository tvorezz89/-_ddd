package microarch.delivery.core.domain.model.order;

import libs.errs.Error;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AssignmentTest {

    private static final UUID ORDER_ID = UUID.randomUUID();

    private static Assignment newAssignedAssignment() {
        return Assignment.mustCreate(ORDER_ID, Volume.mustCreate(10), Location.mustCreate(5, 5));
    }

    private static void setId(Assignment assignment, UUID id) throws Exception {
        Field field = libs.ddd.BaseEntity.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(assignment, id);
    }

    @Test
    void shouldBeCreatedWithCorrectParamsAndAssignedStatus() {
        // Act
        var assignment = newAssignedAssignment();

        // Assert
        assertThat(assignment.getId()).isNotNull();
        assertThat(assignment.getOrderId()).isEqualTo(ORDER_ID);
        assertThat(assignment.getVolume()).isEqualTo(Volume.mustCreate(10));
        assertThat(assignment.getLocation()).isEqualTo(Location.mustCreate(5, 5));
        assertThat(assignment.getStatus()).isEqualTo(AssignmentStatus.ASSIGNED);
    }

    @Test
    void shouldReturnErrorWhenOrderIdIsNullOnCreated() {
        // Act
        var result = Assignment.create(null, Volume.mustCreate(10), Location.mustCreate(5, 5));

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldReturnErrorWhenVolumeIsNullOnCreated() {
        // Act
        var result = Assignment.create(ORDER_ID, null, Location.mustCreate(5, 5));

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldReturnErrorWhenLocationIsNullOnCreated() {
        // Act
        var result = Assignment.create(ORDER_ID, Volume.mustCreate(10), null);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, -1 })
    void shouldReturnErrorWhenVolumeIsNotPositiveOnCreated(int value) {
        // Act
        var result = Volume.create(value);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void volumeShouldBeEqualWhenValuesAreEqual() {
        // Arrange
        var first = Volume.mustCreate(5);
        var second = Volume.mustCreate(5);

        // Act & Assert
        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void shouldBeEqualWhenIdsAreEqual() throws Exception {
        // Arrange: две сущности с одинаковым ID, но разными остальными полями
        UUID sharedId = UUID.randomUUID();
        var first = newAssignedAssignment();
        var second = Assignment.mustCreate(UUID.randomUUID(), Volume.mustCreate(1), Location.mustCreate(1, 1));
        setId(first, sharedId);
        setId(second, sharedId);

        // Act & Assert
        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());

        var third = newAssignedAssignment();
        assertThat(first).isNotEqualTo(third);
    }

    @Test
    void shouldBeCompletableWhenCourierIsAtOrderLocation() {
        // Arrange
        var assignment = newAssignedAssignment();

        // Act
        UnitResult<Error> result = assignment.complete(Location.mustCreate(5, 5));

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(assignment.getStatus()).isEqualTo(AssignmentStatus.COMPLETED);
    }

    @Test
    void shouldBeCompletableWhenCourierIsInOneCellFromOrder() {
        // Arrange
        var assignment = newAssignedAssignment();

        // Act
        UnitResult<Error> result = assignment.complete(Location.mustCreate(5, 6));

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(assignment.getStatus()).isEqualTo(AssignmentStatus.COMPLETED);
    }

    @Test
    void shouldNotBeCompletableWhenCourierIsFarFromOrder() {
        // Arrange
        var assignment = newAssignedAssignment();

        // Act
        UnitResult<Error> result = assignment.complete(Location.mustCreate(7, 7));

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
        assertThat(assignment.getStatus()).isEqualTo(AssignmentStatus.ASSIGNED);
    }

    @Test
    void shouldReturnErrorWhenCompletingAlreadyCompletedAssignment() {
        // Arrange
        var assignment = newAssignedAssignment();
        assignment.complete(Location.mustCreate(5, 5));

        // Act
        UnitResult<Error> result = assignment.complete(Location.mustCreate(5, 5));

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(assignment.getStatus()).isEqualTo(AssignmentStatus.COMPLETED);
    }
}
