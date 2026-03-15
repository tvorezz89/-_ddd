package microarch.delivery.core.application.query;

import libs.errs.Error;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetAllCouriersQuery {

    public static Result<GetAllCouriersQuery, Error> create() {
        return Result.success(new GetAllCouriersQuery());
    }
}
