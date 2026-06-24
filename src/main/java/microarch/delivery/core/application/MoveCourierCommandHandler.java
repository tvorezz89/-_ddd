package microarch.delivery.core.application;

import libs.errs.Error;
import libs.errs.UnitResult;

public interface MoveCourierCommandHandler {
    UnitResult<Error> handle(MoveCourierCommand command);
}
