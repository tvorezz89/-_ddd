package microarch.delivery.core.domain.model.order;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.kernel.Location;
import microarch.delivery.core.domain.kernel.Volume;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    void create_shouldWorkProperly() {
        var id = UUID.randomUUID();
        var location = Location.create(5, 5).getValue();
        var volume = Volume.getCreated(10);
        var result = Order.create(id, location, volume);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getCourierId()).isNull();
        assertThat(result.getValue().getLocation()).isEqualTo(location);
        assertThat(result.getValue().getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(result.getValue().getVolume()).isEqualTo(volume);
        assertThat(result.getValue().getId()).isEqualTo(id);
    }

    @Test
    void manageToCourier_shouldWorkProperly() {
        var courierId = UUID.randomUUID();
        var order = createOrder();

        var result = order.manageToCourier(courierId);

        assertThat(result.isSuccess()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
        assertThat(order.getCourierId()).isEqualTo(courierId);
    }

    @Test
    void complete_shouldWorkProperly() {
        var courierId = UUID.randomUUID();
        var order = createOrder();

        order.manageToCourier(courierId);
        var result = order.complete();

        assertThat(result.isSuccess()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(order.getCourierId()).isEqualTo(courierId);
    }

    private Order createOrder() {
        var id = UUID.randomUUID();
        var location = Location.create(5, 5).getValue();
        var volume = Volume.getCreated(10);
        return Order.create(id, location, volume).getValue();
    }
}
