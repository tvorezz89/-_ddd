package microarch.delivery.core.application.command;

import libs.errs.Error;
import libs.errs.Result;
import java.util.UUID;

public interface CreateOrderCommandHandler {

    Result<UUID, Error> handle(CreateOrderCommand command);

}
