package microarch.delivery.core.domain.model;

import jakarta.persistence.Embeddable;
import libs.ddd.ValueObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Embeddable
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Volume extends ValueObject<Volume> {
    private static final double MIN_VALUE = 0;
    private final double amount;

    public static Volume create(double amount) {
        if (amount <= MIN_VALUE) {
            throw new IllegalArgumentException("Wrong amount of volume");
        }
        return new Volume(amount);
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(this.amount);
    }
}
