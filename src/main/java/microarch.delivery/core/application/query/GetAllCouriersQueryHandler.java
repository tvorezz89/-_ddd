package microarch.delivery.core.application.query;

import libs.errs.Error;
import libs.errs.Result;

public interface GetAllCouriersQueryHandler {

    Result<GetAllCouriersQueryResponse, Error> handle(GetAllCouriersQuery query);
}
