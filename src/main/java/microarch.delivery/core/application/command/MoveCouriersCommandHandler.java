package microarch.delivery.core.application.command;

import libs.errs.Error;
import libs.errs.UnitResult;

public interface MoveCouriersCommandHandler {

    UnitResult<Error> handle(MoveCouriersCommand command);
}
