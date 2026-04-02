package microarch.delivery.core.domain.model.order;

import libs.errs.Error;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum OrderStatus {
    CREATED(1, "created", List.of(2)),
    ASSIGNED(2, "assigned", List.of(3)),
    COMPLETED(3, "completed", List.of()),
    ;

    private final int id;
    private final String name;
    private final List<Integer> nextPossibleStates;

    public static Result<OrderStatus, Error> getByName(String name) {
        for (OrderStatus orderStatus : values()) {
            if (orderStatus.name.equalsIgnoreCase(name)) {
                return Result.success(orderStatus);
            }
        }
        return Result.failure(Errors.orderStatusIsWrong());
    }

    public static Result<OrderStatus, Error> getById(int id) {
        for (OrderStatus orderStatus : values()) {
            if (orderStatus.id == id) {
                return Result.success(orderStatus);
            }
        }
        return Result.failure(Errors.orderStatusIsWrong());
    }

    public Result<OrderStatus, Error> change(OrderStatus newStatus) {
        return this.nextPossibleStates.contains(newStatus.id) ?
                Result.success(newStatus) :
                Result.failure(Errors.incorrectState(this.name, newStatus.name));
    }


    public static class Errors {
        public static Error orderStatusIsWrong() {
            return Error.of("orderStatus.is.wrong", "Incorrect order status. Allowed statuses: "
                    + String.join(", ", Arrays.stream(values()).map(OrderStatus::getName).toList()));
        }

        public static Error incorrectState(String oldStatus, String newStatus) {
            return Error.of(
                    "orderStatus.incorrect.state",
                    String.format("Cannot change from status %s to %s", oldStatus, newStatus)
                            + String.join(", ", Arrays.stream(values()).map(OrderStatus::getName).toList()));
        }
    }
}
