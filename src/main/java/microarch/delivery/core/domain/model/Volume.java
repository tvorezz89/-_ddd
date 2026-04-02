package microarch.delivery.core.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Embeddable
@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Volume extends ValueObject<Volume> {
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 100;

    @Column(name = "size")
    private final int size;

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(this.size);
    }

    public static Result<Volume, Error> create(int size) {
        var error = Guard.againstOutOfRange(size, MIN_VALUE, MAX_VALUE, "size");
        if (error != null) {
            return Result.failure(error);
        }
        return Result.success(new Volume(size));
    }

    public static Volume mustCreate(int size) {
        return create(size).getValueOrThrow();
    }
}
