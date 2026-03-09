package microarch.delivery.core.domain.service;

import microarch.delivery.core.domain.kernel.Location;
import microarch.delivery.core.domain.kernel.Speed;
import microarch.delivery.core.domain.kernel.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderManageServiceImplTest {

    private final OrderManageService service = new OrderManageServiceImpl();

    @Test
    void manage_whenSameLocation_shouldWorkProperly() {
        var david = createCourier("David", Speed.create(1).getValue(), Location.create(1, 1).getValue());
        var sam = createCourier("Sam", Speed.create(2).getValue(), Location.create(1, 1).getValue());
        var john = createCourier("John", Speed.create(3).getValue(), Location.create(1, 1).getValue());
        var couriers = List.of(david, sam, john);
        var order = createOrder();

        var result = service.manage(order, couriers);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isEqualTo(john);
    }

    @Test
    void manage_whenSameSpeed_shouldWorkProperly() {
        var david = createCourier("David", Speed.create(1).getValue(), Location.create(3, 3).getValue());
        var sam = createCourier("Sam", Speed.create(1).getValue(), Location.create(2, 2).getValue());
        var john = createCourier("John", Speed.create(1).getValue(), Location.create(1, 1).getValue());
        var couriers = List.of(david, sam, john);
        var order = createOrder();

        var result = service.manage(order, couriers);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isEqualTo(david);
    }

    @Test
    void manage_shouldWorkProperly() {
        var david = createCourier("David", Speed.create(2).getValue(), Location.create(10, 10).getValue());
        var sam = createCourier("Sam", Speed.create(3).getValue(), Location.create(8, 8).getValue());
        var john = createCourier("John", Speed.create(1).getValue(), Location.create(2, 2).getValue());
        var couriers = List.of(david, sam, john);
        var order = createOrder();

        var result = service.manage(order, couriers);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isEqualTo(sam);
    }

    private Courier createCourier(String name, Speed speed, Location location) {
        return Courier.create(name, speed, location).getValue();
    }

    private Order createOrder() {
        var id = UUID.randomUUID();
        var location = Location.create(5, 5).getValue();
        var volume = Volume.getCreated(10);
        return Order.create(id, location, volume).getValue();
    }
}
