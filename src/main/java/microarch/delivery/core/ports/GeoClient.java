package microarch.delivery.core.ports;

import microarch.delivery.core.domain.model.order.Location;

public interface GeoClient {
    Location getLocation(String street);
}
