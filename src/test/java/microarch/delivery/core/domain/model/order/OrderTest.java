package microarch.delivery.core.domain.model.order;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    private static final UUID ID = UUID.randomUUID();
    private static final Location LOCATION = Location.create(5, 5).getValue();
    private static final Volume VOLUME = Volume.create(3).getValue();

    // --- create ---

    @Test
    void create_shouldSucceed_whenAllParametersAreValid() {
        Result<Order, Error> result = Order.create(ID, LOCATION, VOLUME);
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void create_shouldSetStatusToCreated() {
        Order order = Order.create(ID, LOCATION, VOLUME).getValue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    void create_shouldPreserveId() {
        Order order = Order.create(ID, LOCATION, VOLUME).getValue();
        assertThat(order.getId()).isEqualTo(ID);
    }

    @Test
    void create_shouldFail_whenIdIsNull() {
        Result<Order, Error> result = Order.create(null, LOCATION, VOLUME);
        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void create_shouldFail_whenIdIsEmpty() {
        Result<Order, Error> result = Order.create(new UUID(0L, 0L), LOCATION, VOLUME);
        assertThat(result.isFailure()).isTrue();
    }

    // --- assign ---

    @Test
    void assign_shouldSucceed_whenStatusIsCreated() {
        Order order = Order.create(ID, LOCATION, VOLUME).getValue();
        Result<Order, Error> result = order.assign();
        assertThat(result.isSuccess()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
    }

    @Test
    void assign_shouldFail_whenStatusIsAssigned() {
        Order order = Order.create(ID, LOCATION, VOLUME).getValue();
        order.assign();
        Result<Order, Error> result = order.assign();
        assertThat(result.isFailure()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
    }

    @Test
    void assign_shouldFail_whenStatusIsCompleted() {
        Order order = Order.create(ID, LOCATION, VOLUME).getValue();
        order.assign();
        order.complete();
        Result<Order, Error> result = order.assign();
        assertThat(result.isFailure()).isTrue();
    }

    // --- complete ---

    @Test
    void complete_shouldSucceed_whenStatusIsAssigned() {
        Order order = Order.create(ID, LOCATION, VOLUME).getValue();
        order.assign();
        Result<Order, Error> result = order.complete();
        assertThat(result.isSuccess()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void complete_shouldFail_whenStatusIsCreated() {
        Order order = Order.create(ID, LOCATION, VOLUME).getValue();
        Result<Order, Error> result = order.complete();
        assertThat(result.isFailure()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    void complete_shouldFail_whenStatusIsAlreadyCompleted() {
        Order order = Order.create(ID, LOCATION, VOLUME).getValue();
        order.assign();
        order.complete();
        Result<Order, Error> result = order.complete();
        assertThat(result.isFailure()).isTrue();
    }
}
