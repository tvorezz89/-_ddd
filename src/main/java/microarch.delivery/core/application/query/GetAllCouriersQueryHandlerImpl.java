package microarch.delivery.core.application.query;

import jakarta.persistence.EntityManager;
import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.application.query.dto.GetAllCouriersDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllCouriersQueryHandlerImpl implements GetAllCouriersQueryHandler {

    private static final String QUERY = """
            select c.id, c.name, c.location
            from Courier c
            """;

    private final EntityManager entityManager;

    @Override
    public Result<GetAllCouriersQueryResponse, Error> handle(GetAllCouriersQuery query) {
        var couriers = entityManager.createQuery(QUERY, GetAllCouriersDto.class).getResultList();
        return Result.success(new GetAllCouriersQueryResponse(couriers));
    }
}
