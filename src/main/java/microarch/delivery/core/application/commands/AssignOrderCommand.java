package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AssignOrderCommand {

    public static Result<AssignOrderCommand, Error> create() {
        return Result.success(new AssignOrderCommand());
    }
}
