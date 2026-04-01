package microarch.delivery.core.domain.services;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderDispatchDomainServiceImplTest {

    private final OrderDispatchDomainService service = new OrderDispatchDomainServiceImpl();

    private static Order createOrder(Location location, int volume) {
        return Order.create(UUID.randomUUID(), location, Volume.create(volume).getValue()).getValue();
    }

    private static Courier createCourier(String name, Location location) {
        return Courier.create(name, location).getValue();
    }

    @Test
    void shouldAssignOrderToClosestCourier() {
        Order order = createOrder(Location.create(1, 1).getValue(), 5);
        Courier far = createCourier("Far", Location.create(10, 10).getValue());
        Courier close = createCourier("Close", Location.create(2, 2).getValue());

        Result<Courier, Error> result = service.dispatchOrder(order, List.of(far, close));

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getName()).isEqualTo("Close");
        assertThat(result.getValue().getAssignments()).hasSize(1);
    }

    @Test
    void shouldSkipOverfilledCourier() {
        Order order = createOrder(Location.create(1, 1).getValue(), 5);
        Courier full = createCourier("Full", Location.create(1, 1).getValue());
        full.takeOrder(createOrder(Location.create(1, 1).getValue(), 20)); // заполнен до максимума

        Courier available = createCourier("Available", Location.create(5, 5).getValue());

        Result<Courier, Error> result = service.dispatchOrder(order, List.of(full, available));

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getName()).isEqualTo("Available");
    }

    @Test
    void shouldReturnError_whenAllCouriersOverfilled() {
        Order order = createOrder(Location.create(1, 1).getValue(), 5);
        Courier full1 = createCourier("Full1", Location.create(1, 1).getValue());
        full1.takeOrder(createOrder(Location.create(1, 1).getValue(), 20));

        Courier full2 = createCourier("Full2", Location.create(2, 2).getValue());
        full2.takeOrder(createOrder(Location.create(2, 2).getValue(), 20));

        Result<Courier, Error> result = service.dispatchOrder(order, List.of(full1, full2));

        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void shouldReturnError_whenCourierListIsEmpty() {
        Order order = createOrder(Location.create(1, 1).getValue(), 5);

        Result<Courier, Error> result = service.dispatchOrder(order, List.of());

        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void shouldAssignOrderAndChangeOrderStatus() {
        Order order = createOrder(Location.create(1, 1).getValue(), 5);
        Courier courier = createCourier("Ivan", Location.create(1, 1).getValue());

        Result<Courier, Error> result = service.dispatchOrder(order, List.of(courier));

        assertThat(result.isSuccess()).isTrue();
        assertThat(order.getStatus().name()).isEqualTo("ASSIGNED");
    }
}
