package microarch.delivery.core.ports;

import microarch.delivery.core.domain.model.courier.Courier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Порт для хранения агрегата Courier.
 */
public interface CourierRepository {
    Courier add(Courier courier);

    void update(Courier courier);

    Optional<Courier> findById(UUID courierId);

    List<Courier> getAll();
}
