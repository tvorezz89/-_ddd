package microarch.delivery.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    ASSIGNED("Assigned", "если назначение было назначено"),
    COMPLETED("Completed", "если назначение было завершено");

    public final String name;
    public final String describe;
}
