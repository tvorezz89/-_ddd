package microarch.delivery.core.domain.model.courier;

import libs.errs.DomainInvariantException;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssignmentTest {

    @Test
    void shouldCreateAssignmentInAssignedStatus() {
        var orderId = UUID.randomUUID();
        var volume = Volume.mustCreate(5);
        var location = Location.mustCreate(4, 6);

        var result = Assignment.create(orderId, volume, location);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getId()).isNotNull();
        assertThat(result.getValue().getOrderId()).isEqualTo(orderId);
        assertThat(result.getValue().getVolume()).isEqualTo(volume);
        assertThat(result.getValue().getLocation()).isEqualTo(location);
        assertThat(result.getValue().getStatus()).isEqualTo(AssignmentStatus.ASSIGNED);
    }

    @Test
    void shouldReturnErrorWhenOrderIdIsEmpty() {
        var result = Assignment.create(new UUID(0L, 0L), Volume.mustCreate(1), Location.mustCreate(1, 1));

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldThrowWhenRequiredArgumentsAreNull() {
        assertThatThrownBy(() -> Assignment.create(UUID.randomUUID(), null, Location.mustCreate(1, 1)))
                .isInstanceOf(NullPointerException.class).hasMessage("volume");

        assertThatThrownBy(() -> Assignment.create(UUID.randomUUID(), Volume.mustCreate(1), null))
                .isInstanceOf(NullPointerException.class).hasMessage("location");
    }

    @Test
    void shouldCompleteWhenCourierIsWithinOneCellFromOrderLocation() {
        var assignment = Assignment.mustCreate(UUID.randomUUID(), Volume.mustCreate(3), Location.mustCreate(5, 5));

        var result = assignment.complete(Location.mustCreate(5, 6));

        assertThat(result.isSuccess()).isTrue();
        assertThat(assignment.getStatus()).isEqualTo(AssignmentStatus.COMPLETED);
    }

    @Test
    void shouldReturnErrorWhenCourierIsFarFromOrderLocation() {
        var assignment = Assignment.mustCreate(UUID.randomUUID(), Volume.mustCreate(3), Location.mustCreate(5, 5));

        var result = assignment.complete(Location.mustCreate(7, 5));

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
        assertThat(assignment.getStatus()).isEqualTo(AssignmentStatus.ASSIGNED);
    }

    @Test
    void shouldReturnErrorWhenAssignmentAlreadyCompleted() {
        var assignment = Assignment.mustCreate(UUID.randomUUID(), Volume.mustCreate(3), Location.mustCreate(5, 5));
        assignment.complete(Location.mustCreate(5, 5)).getOrElseThrow();

        var result = assignment.complete(Location.mustCreate(5, 5));

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldBeEqualWhenIdsAreEqual() {
        var id = UUID.randomUUID();
        var first = Assignment.restore(id, UUID.randomUUID(), Volume.mustCreate(1), Location.mustCreate(1, 1),
                AssignmentStatus.ASSIGNED);
        var second = Assignment.restore(id, UUID.randomUUID(), Volume.mustCreate(2), Location.mustCreate(2, 2),
                AssignmentStatus.COMPLETED);

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void shouldFailFastWhenRestoredWithInvalidStatus() {
        assertThatThrownBy(() -> Assignment.restore(UUID.randomUUID(), UUID.randomUUID(), Volume.mustCreate(1),
                Location.mustCreate(1, 1), null)).isInstanceOf(DomainInvariantException.class);
    }

    @Test
    void shouldBeEntity() {
        assertThat(Assignment.class.getSuperclass().getSimpleName()).isEqualTo("BaseEntity");
    }

    @Test
    void shouldHaveImmutableOrderProperties() throws NoSuchFieldException {
        var orderIdField = Assignment.class.getDeclaredField("orderId");
        var volumeField = Assignment.class.getDeclaredField("volume");
        var locationField = Assignment.class.getDeclaredField("location");

        assertThat(Modifier.isFinal(orderIdField.getModifiers())).isTrue();
        assertThat(Modifier.isFinal(volumeField.getModifiers())).isTrue();
        assertThat(Modifier.isFinal(locationField.getModifiers())).isTrue();
    }
}
