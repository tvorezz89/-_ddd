package microarch.delivery.core.application.command;

import libs.errs.Error;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MoveCouriersCommand {

    public static Result<MoveCouriersCommand, Error> create() {
        return Result.success(new MoveCouriersCommand());
    }
}
