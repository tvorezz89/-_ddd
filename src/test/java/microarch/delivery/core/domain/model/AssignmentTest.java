package microarch.delivery.core.domain.model;

import libs.errs.Error;
import libs.errs.Result;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AssignmentTest {

    private static final UUID ORDER_ID = UUID.randomUUID();
    private static final Volume VOLUME = Volume.create(3).getValue();
    private static final Location LOCATION = Location.create(5, 5).getValue();

    // --- create: valid ---

    @Test
    void create_shouldSucceed_whenAllParametersAreValid() {
        Result<Assignment, Error> result = Assignment.create(ORDER_ID, VOLUME, LOCATION);
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void create_shouldSetStatusToAssigned() {
        Assignment assignment = Assignment.create(ORDER_ID, VOLUME, LOCATION).getValue();
        assertThat(assignment.getStatus()).isEqualTo(Status.ASSIGNED);
    }

    @Test
    void create_shouldGenerateId() {
        Assignment assignment = Assignment.create(ORDER_ID, VOLUME, LOCATION).getValue();
        assertThat(assignment.getId()).isNotNull();
    }

    @Test
    void create_shouldSetOrderId() {
        Assignment assignment = Assignment.create(ORDER_ID, VOLUME, LOCATION).getValue();
        assertThat(assignment.getOrderId()).isEqualTo(ORDER_ID);
    }

    @Test
    void create_shouldGenerateUniqueIds() {
        Assignment a1 = Assignment.create(ORDER_ID, VOLUME, LOCATION).getValue();
        Assignment a2 = Assignment.create(ORDER_ID, VOLUME, LOCATION).getValue();
        assertThat(a1.getId()).isNotEqualTo(a2.getId());
    }

    // --- create: invalid ---

    @Test
    void create_shouldFail_whenOrderIdIsNull() {
        Result<Assignment, Error> result = Assignment.create(null, VOLUME, LOCATION);
        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void create_shouldFail_whenOrderIdIsEmpty() {
        Result<Assignment, Error> result = Assignment.create(new UUID(0L, 0L), VOLUME, LOCATION);
        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void create_shouldFail_whenVolumeIsNull() {
        Result<Assignment, Error> result = Assignment.create(ORDER_ID, null, LOCATION);
        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void create_shouldFail_whenLocationIsNull() {
        Result<Assignment, Error> result = Assignment.create(ORDER_ID, VOLUME, null);
        assertThat(result.isFailure()).isTrue();
    }

    // --- equality ---

    @Test
    void equals_shouldReturnTrue_whenSameInstance() {
        Assignment assignment = Assignment.create(ORDER_ID, VOLUME, LOCATION).getValue();
        assertThat(assignment).isEqualTo(assignment);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentInstances() {
        Assignment a1 = Assignment.create(ORDER_ID, VOLUME, LOCATION).getValue();
        Assignment a2 = Assignment.create(ORDER_ID, VOLUME, LOCATION).getValue();
        assertThat(a1).isNotEqualTo(a2);
    }

    // --- finish ---

    @Test
    void finish_shouldSucceed_whenCourierIsAtSameLocation() {
        Assignment assignment = Assignment.create(ORDER_ID, VOLUME, LOCATION).getValue();
        Result<Assignment, Error> result = assignment.finish(LOCATION);
        assertThat(result.isSuccess()).isTrue();
        assertThat(assignment.getStatus()).isEqualTo(Status.COMPLETED);
    }

    @Test
    void finish_shouldSucceed_whenCourierIsOneStepAway() {
        Assignment assignment = Assignment.create(ORDER_ID, VOLUME, LOCATION).getValue();
        Location oneStepAway = Location.create(5, 6).getValue();
        Result<Assignment, Error> result = assignment.finish(oneStepAway);
        assertThat(result.isSuccess()).isTrue();
        assertThat(assignment.getStatus()).isEqualTo(Status.COMPLETED);
    }

    @Test
    void finish_shouldFail_whenCourierIsTooFarAway() {
        Assignment assignment = Assignment.create(ORDER_ID, VOLUME, LOCATION).getValue();
        Location farAway = Location.create(1, 1).getValue();
        Result<Assignment, Error> result = assignment.finish(farAway);
        assertThat(result.isFailure()).isTrue();
        assertThat(assignment.getStatus()).isEqualTo(Status.ASSIGNED);
    }

    @Test
    void finish_shouldFail_whenCourierIsTwoStepsAway() {
        Assignment assignment = Assignment.create(ORDER_ID, VOLUME, LOCATION).getValue();
        Location twoStepsAway = Location.create(5, 7).getValue();
        Result<Assignment, Error> result = assignment.finish(twoStepsAway);
        assertThat(result.isFailure()).isTrue();
    }
}
