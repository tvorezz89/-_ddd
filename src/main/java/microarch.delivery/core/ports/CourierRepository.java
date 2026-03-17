package microarch.delivery.core.ports;

import microarch.delivery.core.domain.model.courier.Courier;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourierRepository {

    void add(Courier courier);

    void update(Courier courier);

    void updateAll(Collection<Courier> couriers);

    Optional<Courier> findById(UUID id);

    List<Courier> findAllById(Collection<UUID> ids);

    List<Courier> findAllFreeCouriers();
}
