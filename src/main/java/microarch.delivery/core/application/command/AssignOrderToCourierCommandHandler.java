package microarch.delivery.core.application.command;

import libs.errs.Error;
import libs.errs.UnitResult;

public interface AssignOrderToCourierCommandHandler {

    UnitResult<Error> handle(AssignOrderToCourierCommand command);
}
