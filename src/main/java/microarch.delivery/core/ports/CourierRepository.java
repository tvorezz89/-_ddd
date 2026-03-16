package microarch.delivery.core.ports;

import microarch.delivery.core.domain.model.courier.Courier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourierRepository {

    Courier save(Courier order);

    Courier update(Courier order);

    Optional<Courier> findById(UUID id);

    List<Courier> findAllFreeCouriers();

    List<Courier> findAll();

}
