package microarch.delivery.adapters.out;

import microarch.delivery.adapters.out.postgres.CourierJpaRepository;
import microarch.delivery.core.domain.kernel.Location;
import microarch.delivery.core.domain.kernel.Speed;
import microarch.delivery.core.domain.kernel.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CourierRepositoryIntegrationTest extends BasePostgresContainerTest {

    @Autowired
    CourierRepository repository;

    @Autowired
    CourierJpaRepository courierJpaRepository;

    @BeforeEach
    void clear() {
        courierJpaRepository.deleteAll();
    }

    @Test
    void add_shouldWorkProperly() {
        var name = "David";
        var speed = Speed.create(3).getValue();
        var location = Location.create(5, 5).getValue();
        var courier = Courier.create(name, speed, location).getValue();

        repository.add(courier);

        var resultOpt = repository.findById(courier.getId());

        assertThat(resultOpt.isPresent()).isTrue();
        var result = resultOpt.get();
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getSpeed()).isEqualTo(speed);
        assertThat(result.getLocation()).isEqualTo(location);
        assertThat(result.equals(courier)).isTrue();
    }

    @Test
    void update_shouldWorkProperly() {
        var courier = createCourier();
        repository.add(courier);

        var newLocation = Location.create(8, 5).getValue();
        courier.move(newLocation);

        repository.update(courier);

        var resultOpt = repository.findById(courier.getId());

        assertThat(resultOpt.isPresent()).isTrue();

        var result = resultOpt.get();

        assertThat(result.getLocation()).isEqualTo(newLocation);
    }

    @Test
    void findAllFreeCouriers_shouldWorkProperly() {
        var courier1 = createCourier();
        var courier2 = createCourier();
        repository.add(courier1);
        repository.add(courier2);

        var courier3 = createCourier();
        repository.add(courier3);

        var order3 = createOrder();
        courier3.takeAnOrder(order3);
        repository.update(courier3);
        var result = repository.findAllFreeCouriers();

        assertThat(result.size()).isEqualTo(2);
        assertThat(result).containsExactlyInAnyOrder(courier1, courier2);
    }

    private Courier createCourier() {
        var name = "David";
        var speed = Speed.create(3).getValue();
        var location = Location.create(5, 5).getValue();
        return Courier.create(name, speed, location).getValue();
    }

    private Order createOrder() {
        var id = UUID.randomUUID();
        var location = Location.create(5, 5).getValue();
        var volume = Volume.create(5).getValue();
        return Order.create(id, location, volume).getValue();
    }
}
