package microarch.delivery.core.application.query;

import libs.errs.Error;
import libs.errs.Result;

public interface GetAllUnFinishedOrdersHandler {

    Result<GetAllUnFinishedOrdersResponseDto, Error> handle();

}
