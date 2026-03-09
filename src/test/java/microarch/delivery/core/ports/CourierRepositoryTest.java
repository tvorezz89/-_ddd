package microarch.delivery.core.ports;

import jakarta.transaction.Transactional;
import microarch.delivery.adapters.out.postgres.CourierJpaRepository;
import microarch.delivery.core.domain.Location;
import microarch.delivery.core.domain.Speed;
import microarch.delivery.core.domain.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.utils.BasePostgresContainerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class CourierRepositoryTest extends BasePostgresContainerTest {

    @Autowired
    CourierRepository repository;

    @Autowired
    CourierJpaRepository jpaRepository;

    @BeforeEach
    void clear() {
        jpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Поиск свободных курьеров")
    void find_all_free_couriers_when_all_places_free() {
        Courier freeCourier1 = Courier.mustCreate(
                "Free Courier",
                Speed.mustCreate(10),
                Location.mustCreate(1, 1)
        );
        freeCourier1.addStoragePlace("Free Box", Volume.mustCreate(20));

        Courier freeCourier2 = Courier.mustCreate(
                "Busy Courier",
                Speed.mustCreate(8),
                Location.mustCreate(2, 3)
        );
        freeCourier2.addStoragePlace("Busy Box", Volume.mustCreate(10));

        repository.save(freeCourier1);
        repository.save(freeCourier2);

        List<Courier> freeCouriers = repository.findAllFreeCouriers();

        assertThat(freeCouriers).hasSize(2);
    }


    @Test
    @DisplayName("Поиск свободных курьеров")
    void find_all_free_couriers_when_one_is_free() {
        Courier freeCourier1 = Courier.mustCreate(
                "Free Courier",
                Speed.mustCreate(10),
                Location.mustCreate(1, 1)
        );
        freeCourier1.addStoragePlace("Free Box", Volume.mustCreate(20));

        Courier freeCourier2 = Courier.mustCreate(
                "Busy Courier",
                Speed.mustCreate(8),
                Location.mustCreate(2, 3)
        );
        freeCourier2.addStoragePlace("Busy Box", Volume.mustCreate(10));

        repository.save(freeCourier1);
        repository.save(freeCourier2);

        List<Courier> freeCouriers = repository.findAllFreeCouriers();

        assertThat(freeCouriers).hasSize(2);
    }


    @Test
    @DisplayName("Сохранение и поиск курьера по ID")
    void save_and_find_courier_by_id() {
        Courier courier = Courier.mustCreate(
                "Test Courier",
                Speed.mustCreate(10),
                Location.mustCreate(1, 1)
        );

        repository.save(courier);

        Optional<Courier> loaded = repository.findById(courier.getId());

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getName()).isEqualTo("Test Courier");
        assertThat(loaded.get().getStoragePlaces()).hasSize(1);
    }


    @Test
    @DisplayName("Обновление курьера")
    void update_courier() {
        Courier updated = Courier.mustCreate(
                "Old Name",
                Speed.mustCreate(5),
                Location.mustCreate(5, 6)
        );
        repository.save(updated);

        updated.addStoragePlace("Test Box", Volume.mustCreate(20));

        repository.update(updated);
        Optional<Courier> loaded = repository.findById(updated.getId());

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getStoragePlaces()).hasSize(2);
    }

}
