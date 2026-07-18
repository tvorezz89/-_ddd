package microarch.delivery.adapters.out.grpc;

import java.util.concurrent.TimeUnit;
import clients.geo.GeoGrpc;
import clients.geo.GeoProto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import microarch.delivery.ApplicationProperties;
import microarch.delivery.core.domain.model.order.Location;
import microarch.delivery.core.ports.GeoClient;
import org.springframework.stereotype.Component;

@Component
public class GrpcGeoClient implements GeoClient {
    private final ManagedChannel channel;
    private final GeoGrpc.GeoBlockingStub geo;

    public GrpcGeoClient(ApplicationProperties properties1) {
        var geoService = properties1.getGrpc().getGeoService();
        channel = ManagedChannelBuilder.forAddress(geoService.getHost(), geoService.getPort()).usePlaintext().build();
        geo = GeoGrpc.newBlockingStub(channel);
    }

    @Override
    public Location getLocation(String street) {
        var request = GeoProto.GetGeolocationRequest.newBuilder().setStreet(street).build();
        var response = geo.withDeadlineAfter(3, TimeUnit.SECONDS).getGeolocation(request);
        return Location.of(response.getLocation().getX(), response.getLocation().getY());
    }

    @PreDestroy
    public void close() {
        channel.shutdown();
    }
}
