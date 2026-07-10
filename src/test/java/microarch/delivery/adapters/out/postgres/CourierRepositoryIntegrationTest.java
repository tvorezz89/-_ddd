package microarch.delivery.adapters.out.postgres;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.courier.AssignmentStatus;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CourierRepositoryIntegrationTest extends PostgresIntegrationTestBase {

    @Autowired
    CourierRepository repository;

    @Test
    void shouldAddAndFindCourierById() {
        var courier = Courier.mustCreate("Ivan", Location.mustCreate(1, 2));

        repository.add(courier);

        var loaded = repository.findById(courier.getId());
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getId()).isEqualTo(courier.getId());
        assertThat(loaded.get().getName()).isEqualTo(courier.getName());
        assertThat(loaded.get().getLocation()).isEqualTo(courier.getLocation());
        assertThat(loaded.get().getMaxVolume()).isEqualTo(courier.getMaxVolume());
        assertThat(loaded.get().getAssignments()).isEmpty();
    }

    @Test
    void shouldUpdateCourierWithAssignment() {
        var courier = Courier.mustCreate("Petr", Location.mustCreate(3, 4));
        var orderId = UUID.randomUUID();
        repository.add(courier);
        courier.assign(orderId, Volume.mustCreate(5), Location.mustCreate(6, 7)).getOrElseThrow();

        repository.update(courier);

        var loaded = repository.findById(courier.getId());
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getAssignments()).hasSize(1);
        assertThat(loaded.get().getAssignments().getFirst().getOrderId()).isEqualTo(orderId);
        assertThat(loaded.get().getAssignments().getFirst().getStatus()).isEqualTo(AssignmentStatus.ASSIGNED);
    }

    @Test
    void shouldFindAllCouriers() {
        var first = Courier.mustCreate("First", Location.mustCreate(1, 1));
        var second = Courier.mustCreate("Second", Location.mustCreate(2, 2));
        repository.add(first);
        repository.add(second);

        var loaded = repository.findAll();

        assertThat(loaded).extracting(Courier::getId).containsExactlyInAnyOrder(first.getId(), second.getId());
    }
}
