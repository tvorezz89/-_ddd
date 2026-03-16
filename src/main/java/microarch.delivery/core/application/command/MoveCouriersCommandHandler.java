package microarch.delivery.core.application.command;

import libs.errs.UnitResult;
import libs.errs.Error;

public interface MoveCouriersCommandHandler {

    UnitResult<Error> handle();
}
