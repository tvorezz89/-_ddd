package microarch.delivery.adapters.out.postgres;

import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CourierRepositoryImpl implements CourierRepository {

    private final CourierJpaRepository repository;

    @Override
    public void add(Courier courier) {
        repository.save(courier);
    }

    @Override
    public void update(Courier courier) {
        repository.save(courier);
    }

    @Override
    public void updateAll(Collection<Courier> couriers) {
        repository.saveAll(couriers);
    }

    @Override
    public Optional<Courier> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Courier> findAllById(Collection<UUID> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public List<Courier> findAllFreeCouriers() {
        return repository.findAllFreeCouriers();
    }
}
