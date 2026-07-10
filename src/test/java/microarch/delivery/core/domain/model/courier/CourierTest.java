package microarch.delivery.core.domain.model.courier;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CourierTest {

    @Test
    void shouldCreateCourierWithMaxVolumeTwenty() {
        var location = Location.mustCreate(4, 5);

        var result = Courier.create("Ivan", location);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getId()).isNotNull();
        assertThat(result.getValue().getName()).isEqualTo("Ivan");
        assertThat(result.getValue().getLocation()).isEqualTo(location);
        assertThat(result.getValue().getMaxVolume()).isEqualTo(Volume.mustCreate(20));
        assertThat(result.getValue().getAssignments()).isEmpty();
    }

    @Test
    void shouldReturnErrorWhenNameIsBlank() {
        var result = Courier.create(" ", Location.mustCreate(1, 1));

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldThrowWhenLocationIsNull() {
        assertThatThrownBy(() -> Courier.create("Ivan", null)).isInstanceOf(NullPointerException.class)
                .hasMessage("location");
    }

    @Test
    void shouldKnowWhetherCanAssignNewOrder() {
        var courier = Courier.mustCreate("Ivan", Location.mustCreate(1, 1));
        courier.assign(UUID.randomUUID(), Volume.mustCreate(12), Location.mustCreate(2, 2)).getOrElseThrow();

        assertThat(courier.canAssign(Volume.mustCreate(8))).isTrue();
        assertThat(courier.canAssign(Volume.mustCreate(9))).isFalse();
    }

    @Test
    void shouldCreateAssignmentWhenCapacityAllows() {
        var courier = Courier.mustCreate("Ivan", Location.mustCreate(1, 1));
        var orderId = UUID.randomUUID();
        var orderLocation = Location.mustCreate(7, 8);

        var result = courier.assign(orderId, Volume.mustCreate(5), orderLocation);

        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.getAssignments()).hasSize(1);
        assertThat(courier.getAssignments().getFirst().getOrderId()).isEqualTo(orderId);
        assertThat(courier.getAssignments().getFirst().getLocation()).isEqualTo(orderLocation);
        assertThat(courier.getAssignments().getFirst().getStatus()).isEqualTo(AssignmentStatus.ASSIGNED);
    }

    @Test
    void shouldReturnErrorWhenCapacityIsExceeded() {
        var courier = Courier.mustCreate("Ivan", Location.mustCreate(1, 1));
        courier.assign(UUID.randomUUID(), Volume.mustCreate(15), Location.mustCreate(2, 2)).getOrElseThrow();

        var result = courier.assign(UUID.randomUUID(), Volume.mustCreate(6), Location.mustCreate(3, 3));

        assertThat(result.isFailure()).isTrue();
        assertThat(courier.getAssignments()).hasSize(1);
    }

    @Test
    void shouldMoveToValidLocation() {
        var courier = Courier.mustCreate("Ivan", Location.mustCreate(1, 1));
        var newLocation = Location.mustCreate(3, 4);

        var result = courier.moveTo(newLocation);

        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.getLocation()).isEqualTo(newLocation);
    }

    @Test
    void shouldCompleteAssignmentWhenCourierIsCloseEnough() {
        var courier = Courier.mustCreate("Ivan", Location.mustCreate(5, 5));
        var orderId = UUID.randomUUID();
        courier.assign(orderId, Volume.mustCreate(5), Location.mustCreate(5, 6)).getOrElseThrow();

        var result = courier.completeAssignment(orderId);

        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.getAssignments().getFirst().getStatus()).isEqualTo(AssignmentStatus.COMPLETED);
        assertThat(courier.canAssign(Volume.mustCreate(20))).isTrue();
    }

    @Test
    void shouldReturnErrorWhenCompletingUnknownAssignment() {
        var courier = Courier.mustCreate("Ivan", Location.mustCreate(5, 5));

        var result = courier.completeAssignment(UUID.randomUUID());

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldReturnErrorWhenCompletingAssignmentFromFarLocation() {
        var courier = Courier.mustCreate("Ivan", Location.mustCreate(1, 1));
        var orderId = UUID.randomUUID();
        courier.assign(orderId, Volume.mustCreate(5), Location.mustCreate(5, 5)).getOrElseThrow();

        var result = courier.completeAssignment(orderId);

        assertThat(result.isFailure()).isTrue();
        assertThat(courier.getAssignments().getFirst().getStatus()).isEqualTo(AssignmentStatus.ASSIGNED);
    }

    @Test
    void shouldBeAggregate() {
        assertThat(Courier.class.getSuperclass().getSimpleName()).isEqualTo("Aggregate");
    }

    @Test
    void shouldHaveImmutableBusinessIdentityProperties() throws NoSuchFieldException {
        var nameField = Courier.class.getDeclaredField("name");
        var maxVolumeField = Courier.class.getDeclaredField("maxVolume");

        assertThat(Modifier.isFinal(nameField.getModifiers())).isTrue();
        assertThat(Modifier.isFinal(maxVolumeField.getModifiers())).isTrue();
    }
}
