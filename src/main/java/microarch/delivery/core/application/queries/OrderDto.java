package microarch.delivery.core.application.queries;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class OrderDto {
    private final UUID id;
    private final int locationX;
    private final int locationY;
}
