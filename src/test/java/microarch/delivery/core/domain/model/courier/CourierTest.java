package microarch.delivery.core.domain.model.courier;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.Assignment;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Order;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CourierTest {

    private static final String NAME = "Ivan";
    private static final Location LOCATION = Location.create(5, 5).getValue();

    private static Order createOrder(int volume) {
        return Order.create(UUID.randomUUID(), LOCATION, Volume.create(volume).getValue()).getValue();
    }

    // --- create ---

    @Test
    void create_shouldSucceed_whenAllParametersAreValid() {
        Result<Courier, Error> result = Courier.create(NAME, LOCATION);
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void create_shouldSetMaxVolumeTo20() {
        Courier courier = Courier.create(NAME, LOCATION).getValue();
        assertThat(courier.getMaxVolume().getVolume()).isEqualTo(20);
    }

    @Test
    void create_shouldHaveEmptyAssignments() {
        Courier courier = Courier.create(NAME, LOCATION).getValue();
        assertThat(courier.getAssignments()).isEmpty();
    }

    @Test
    void create_shouldFail_whenNameIsNull() {
        Result<Courier, Error> result = Courier.create(null, LOCATION);
        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void create_shouldFail_whenNameIsBlank() {
        Result<Courier, Error> result = Courier.create("  ", LOCATION);
        assertThat(result.isFailure()).isTrue();
    }

    // --- takeOrder ---

    @Test
    void takeOrder_shouldSucceed_whenVolumeIsWithinLimit() {
        Courier courier = Courier.create(NAME, LOCATION).getValue();
        Order order = createOrder(5);
        Result<Courier, Error> result = courier.takeOrder(order);
        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.getAssignments()).hasSize(1);
    }

    @Test
    void takeOrder_shouldSetOrderIdInAssignment() {
        Courier courier = Courier.create(NAME, LOCATION).getValue();
        Order order = createOrder(5);
        courier.takeOrder(order);
        assertThat(courier.getAssignments().get(0).getOrderId()).isEqualTo(order.getId());
    }

    @Test
    void takeOrder_shouldFail_whenVolumeExceedsLimit() {
        Courier courier = Courier.create(NAME, LOCATION).getValue();
        Result<Courier, Error> result = courier.takeOrder(createOrder(21));
        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void takeOrder_shouldFail_whenSumOfVolumesExceedsLimit() {
        Courier courier = Courier.create(NAME, LOCATION).getValue();
        courier.takeOrder(createOrder(15));
        Result<Courier, Error> result = courier.takeOrder(createOrder(6));
        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void takeOrder_shouldSucceed_whenSumOfVolumesEqualsLimit() {
        Courier courier = Courier.create(NAME, LOCATION).getValue();
        courier.takeOrder(createOrder(10));
        Result<Courier, Error> result = courier.takeOrder(createOrder(10));
        assertThat(result.isSuccess()).isTrue();
    }

    // --- finishAssignment ---

    @Test
    void finishAssignment_shouldSucceed_whenCourierIsAtOrderLocation() {
        Courier courier = Courier.create(NAME, LOCATION).getValue();
        Order order = createOrder(5);
        courier.takeOrder(order);
        Result<Courier, Error> result = courier.finishAssignment(order.getId());
        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.getAssignments()).isEmpty();
    }

    @Test
    void finishAssignment_shouldFail_whenCourierIsTooFarAway() {
        Courier courier = Courier.create(NAME, LOCATION).getValue();
        Order order = Order.create(UUID.randomUUID(), Location.create(1, 1).getValue(), Volume.create(5).getValue())
                .getValue();
        courier.takeOrder(order);
        Result<Courier, Error> result = courier.finishAssignment(order.getId());
        assertThat(result.isFailure()).isTrue();
        assertThat(courier.getAssignments()).hasSize(1);
    }

    @Test
    void finishAssignment_shouldFail_whenAssignmentNotFound() {
        Courier courier = Courier.create(NAME, LOCATION).getValue();
        Result<Courier, Error> result = courier.finishAssignment(UUID.randomUUID());
        assertThat(result.isFailure()).isTrue();
    }

    // --- changeLocation ---

    @Test
    void changeLocation_shouldUpdateLocation() {
        Courier courier = Courier.create(NAME, LOCATION).getValue();
        Location newLocation = Location.create(3, 3).getValue();
        courier.changeLocation(newLocation);
        assertThat(courier.getLocation()).isEqualTo(newLocation);
    }
}
