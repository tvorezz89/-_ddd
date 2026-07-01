package microarch.delivery.adapters.out.postgres;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PostgresCourierRepository implements CourierRepository {
    private final CourierJpaRepository _couriers;

    public PostgresCourierRepository(CourierJpaRepository couriers) {
        _couriers = couriers;
    }

    @Override
    public void add(Courier courier) {
        _couriers.save(CourierJpaEntity.fromDomain(courier));
    }

    @Override
    public void update(Courier courier) {
        _couriers.save(CourierJpaEntity.fromDomain(courier));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Courier> getById(UUID id) {
        return _couriers.findById(id).map(CourierJpaEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Courier> getAll() {
        return _couriers.findAll().stream()
                .map(CourierJpaEntity::toDomain)
                .toList();
    }
}
