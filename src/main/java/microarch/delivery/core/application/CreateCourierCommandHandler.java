package microarch.delivery.core.application;

import libs.errs.Error;
import libs.errs.Result;

import java.util.UUID;

public interface CreateCourierCommandHandler {
    Result<UUID, Error> handle(CreateCourierCommand command);
}
