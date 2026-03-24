package microarch.delivery.adapters.out.grpc;

import clients.geo.GeoGrpc;
import clients.geo.GeoProto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import microarch.delivery.ApplicationProperties;
import microarch.delivery.core.domain.Address;
import microarch.delivery.core.domain.Location;
import microarch.delivery.core.ports.GeoClient;
import org.springframework.stereotype.Service;

@Service
public class GeoClientImpl implements GeoClient {
    private final ManagedChannel channel;
    private final GeoGrpc.GeoBlockingStub stub;

    public GeoClientImpl(ApplicationProperties properties) {
        this.channel = ManagedChannelBuilder
                .forAddress(properties.getGrpc().getGeoService().getHost(),
                        properties.getGrpc().getGeoService().getPort())
                .usePlaintext()
                .build();
        this.stub = GeoGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdown() {
        if (!channel.isShutdown()) {
            channel.shutdown();
        }
    }

    @Override
    public Location getGeo(Address address) {
        var request = GeoProto.GetGeolocationRequest.newBuilder()
                .setStreet(address.getStreet())
                .build();

        GeoProto.GetGeolocationReply response = stub.getGeolocation(request);

        var geoLocation = response.getLocation();

        return Location.mustCreate(geoLocation.getX(), geoLocation.getY());
    }
}
