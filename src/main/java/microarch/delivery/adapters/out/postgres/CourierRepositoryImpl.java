package microarch.delivery.adapters.out.postgres;

import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CourierRepositoryImpl implements CourierRepository {

    private final CourierJpaRepository jpa;

    public CourierRepositoryImpl(CourierJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Courier courier) {
        this.jpa.save(courier);
    }

    @Override
    public Optional<Courier> findById(UUID id) {
        return this.jpa.findById(id);
    }

    @Override
    public List<Courier> findAll() {
        return this.jpa.findAll();
    }
}
