package microarch.delivery.core.application.query;

import libs.errs.Error;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetAllNotCompletedOrdersQuery {

    public static Result<GetAllNotCompletedOrdersQuery, Error> create() {
        return Result.success(new GetAllNotCompletedOrdersQuery());
    }
}
