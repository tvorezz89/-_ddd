package microarch.delivery.core.domain.model.courier;

import microarch.delivery.core.domain.kernel.Location;
import microarch.delivery.core.domain.kernel.Speed;
import microarch.delivery.core.domain.kernel.Volume;
import microarch.delivery.core.domain.model.order.Order;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CourierTest {

    @Test
    void create_shouldWorkProperly() {
        var name = "David";
        var speed = Speed.create(2).getValue();
        var location = Location.create(5, 5).getValue();

        var result = Courier.create(name, speed, location);
        var storages = result.getValue().getStoragePlaces();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getName()).isEqualTo(name);
        assertThat(result.getValue().getSpeed()).isEqualTo(speed);
        assertThat(result.getValue().getLocation()).isEqualTo(location);
        assertThat(result.getValue().getId()).isNotNull();
        assertThat(storages.size()).isEqualTo(1);
        assertThat(storages.getFirst()).satisfies(storage -> {
            assertThat(storage.getId()).isNotNull();
            assertThat(storage.getOrderId()).isNull();
            assertThat(storage.getTotalVolume().getVolume()).isEqualTo(10);
            assertThat(storage.getName()).isEqualTo("Сумка");
        });
    }

    @Test
    void addStorage_shouldWorkProperly() {
        var courier = createCourier();

        var storageName = "Пакет";
        var storageVolume = Volume.getCreated(10);

        var result = courier.addStorage(storageName, storageVolume);

        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.getStoragePlaces().size()).isEqualTo(2);
    }

    @Test
    void canTakeAnOrder_shouldWorkProperly() {
        var courier = createCourier();
        var order = createOrder();

        var result = courier.canTakeAnOrder(order);

        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void takeAnOrder_shouldWorkProperly() {
        var courier = createCourier();
        var order = createOrder();

        var result = courier.takeAnOrder(order);

        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.getStoragePlaces().getFirst().getOrderId()).isEqualTo(order.getId());
    }

    @Test
    void completeAnOrder_shouldWorkProperly() {
        var courier = createCourier();
        var order = createOrder();

        courier.takeAnOrder(order);
        var result = courier.completeAnOrder(order);

        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.getStoragePlaces().getFirst().getOrderId()).isNull();
    }

    @Test
    void movesCount_shouldWorkProperly() {
        var movesCount = 4;
        var courier = createCourier();
        var target = Location.create(5, 5).getValue();

        var result = courier.movesCount(target);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isEqualTo(movesCount);
    }

    @Test
    void move_shouldWortProperly() {
        var courier = createCourier();
        var target = Location.create(5, 5).getValue();
        var location = Location.create(3, 2).getValue();

        var result = courier.move(target);

        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.getLocation()).isEqualTo(location);
    }

    private Courier createCourier() {
        var name = "David";
        var speed = Speed.create(2).getValue();
        var location = Location.create(1, 2).getValue();

        return Courier.create(name, speed, location).getValue();
    }

    private Order createOrder() {
        var id = UUID.randomUUID();
        var location = Location.create(5, 5).getValue();
        var volume = Volume.getCreated(10);
        return Order.create(id, location, volume).getValue();
    }
}
