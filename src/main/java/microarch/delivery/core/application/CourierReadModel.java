package microarch.delivery.core.application;

import java.util.UUID;

public record CourierReadModel(UUID id, String name, int x, int y) {
}
