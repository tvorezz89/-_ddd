package microarch.delivery.adapters.out.postgres;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CourierRepositoryIntegrationTest extends PostgresIntegrationTestBase {

    @Autowired
    CourierRepository repository;

    @Autowired
    CourierJpaRepository jpa;

    @BeforeEach
    void cleanUp() {
        jpa.deleteAll();
    }

    @Test
    void canAddAndGetCourierById() {
        // Arrange
        var courier = Courier.mustCreate("Иван", Location.mustCreate(3, 4));

        // Act
        repository.add(courier);
        var loaded = repository.findById(courier.getId());

        // Assert
        assertThat(loaded).isPresent();
        var restored = loaded.get();
        assertThat(restored.getName()).isEqualTo("Иван");
        assertThat(restored.getLocation()).isEqualTo(Location.mustCreate(3, 4));
        assertThat(restored.getMaxVolume()).isEqualTo(Volume.mustCreate(Courier.MAX_VOLUME_VALUE));
        assertThat(restored.getAssignments()).isEmpty();
    }

    @Test
    void canUpdateCourierWithAssignments() {
        // Arrange
        var courier = Courier.mustCreate("Пётр", Location.mustCreate(1, 1));
        repository.add(courier);
        var orderId = UUID.randomUUID();
        courier.addAssignment(orderId, Volume.mustCreate(10));
        courier.move(Location.mustCreate(2, 1));

        // Act
        repository.update(courier);
        var loaded = repository.findById(courier.getId());

        // Assert
        assertThat(loaded).isPresent();
        var restored = loaded.get();
        assertThat(restored.getLocation()).isEqualTo(Location.mustCreate(2, 1));
        assertThat(restored.getAssignments()).hasSize(1);
        assertThat(restored.hasAssignment(orderId)).isTrue();
        assertThat(restored.getTakenVolume()).isEqualTo(Volume.mustCreate(10));
    }

    @Test
    void canRemoveAssignmentViaUpdate() {
        // Arrange
        var courier = Courier.mustCreate("Анна", Location.mustCreate(5, 5));
        var orderId = UUID.randomUUID();
        courier.addAssignment(orderId, Volume.mustCreate(5));
        repository.add(courier);
        courier.removeAssignment(orderId);

        // Act
        repository.update(courier);
        var loaded = repository.findById(courier.getId());

        // Assert
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getAssignments()).isEmpty();
    }

    @Test
    void canGetAllCouriers() {
        // Arrange
        var courier1 = Courier.mustCreate("Курьер 1", Location.mustCreate(1, 1));
        var courier2 = Courier.mustCreate("Курьер 2", Location.mustCreate(9, 9));
        repository.add(courier1);
        repository.add(courier2);

        // Act
        List<Courier> loaded = repository.getAll();

        // Assert
        assertThat(loaded).extracting(Courier::getId).contains(courier1.getId(), courier2.getId());
    }
}
