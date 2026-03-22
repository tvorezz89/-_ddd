package microarch.delivery.core.ports;


import microarch.delivery.core.domain.Address;
import microarch.delivery.core.domain.Location;

public interface GeoClient {

    Location getGeo(Address address);
}
