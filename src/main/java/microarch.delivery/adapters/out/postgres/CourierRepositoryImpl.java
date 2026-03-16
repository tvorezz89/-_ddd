package microarch.delivery.adapters.out.postgres;

import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CourierRepositoryImpl implements CourierRepository {

    private final CourierJpaRepository jpaRepository;

    @Override
    public Courier save(Courier order) {
        return jpaRepository.save(order);
    }

    @Override
    public Courier update(Courier order) {
        return jpaRepository.save(order);
    }

    @Override
    public Optional<Courier> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Courier> findAllFreeCouriers() {
        return jpaRepository.findAllFreeCouriers();
    }

    @Override
    public List<Courier> findAll() {
        return jpaRepository.findAll();
    }
}
