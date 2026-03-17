package microarch.delivery.core.application.query;

import libs.errs.Error;
import libs.errs.Result;

public interface GetAllNotCompletedOrdersQueryHandler {

    Result<GetAllNotCompletedOrdersQueryResponse, Error> handle(GetAllNotCompletedOrdersQuery query);
}
