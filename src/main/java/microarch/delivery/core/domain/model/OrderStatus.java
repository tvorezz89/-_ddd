package microarch.delivery.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    CREATED("Created"), ASSIGNED("Assigned"), COMPLETED("Completed");

    private final String name;
}
