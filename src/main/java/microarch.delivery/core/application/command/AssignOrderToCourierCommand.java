package microarch.delivery.core.application.command;

import libs.errs.Error;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AssignOrderToCourierCommand {

    public static Result<AssignOrderToCourierCommand, Error> create() {
        return Result.success(new AssignOrderToCourierCommand());
    }
}
