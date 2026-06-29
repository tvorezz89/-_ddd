package microarch.delivery.adapters.in.http;

final class HttpMapper {
    private HttpMapper() {
    }

    static microarch.delivery.adapters.in.http.model.Location toHttp(
            microarch.delivery.core.domain.model.Location location) {
        return new microarch.delivery.adapters.in.http.model.Location(location.getX(), location.getY());
    }
}
