package microarch.delivery.core.application.queries;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CourierDto {
    private final UUID id;
    private final String name;
    private final int locationX;
    private final int locationY;
}
