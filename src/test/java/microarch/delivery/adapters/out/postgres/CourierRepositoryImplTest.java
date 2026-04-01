package microarch.delivery.adapters.out.postgres;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CourierRepositoryImplTest extends PostgresIntegrationTestBase {

    @Autowired
    private CourierRepository courierRepository;

    private Courier createCourier(String name) {
        return Courier.create(name, Location.create(3, 3).getValue()).getValue();
    }

    @Test
    void save_shouldPersistNewCourier() {
        Courier courier = createCourier("Иван");

        courierRepository.save(courier);

        var found = courierRepository.findById(courier.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Иван");
    }

    @Test
    void findById_shouldReturnEmpty_whenCourierDoesNotExist() {
        var found = courierRepository.findById(UUID.randomUUID());
        assertThat(found).isEmpty();
    }

    @Test
    void save_shouldUpdateExistingCourier() {
        Courier courier = createCourier("Иван");
        courierRepository.save(courier);

        Location newLocation = Location.create(7, 7).getValue();
        courier.changeLocation(newLocation);
        courierRepository.save(courier);

        var found = courierRepository.findById(courier.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getLocation().getX()).isEqualTo(7);
        assertThat(found.get().getLocation().getY()).isEqualTo(7);
    }

    @Test
    void findAll_shouldReturnAllCouriers() {
        Courier c1 = createCourier("Иван");
        Courier c2 = createCourier("Петр");
        Courier c3 = createCourier("Анна");

        courierRepository.save(c1);
        courierRepository.save(c2);
        courierRepository.save(c3);

        var all = courierRepository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(3);
        assertThat(all).extracting(Courier::getName).contains("Иван", "Петр", "Анна");
    }
}
