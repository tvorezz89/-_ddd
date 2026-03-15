package microarch.delivery.core.application.query;

import jakarta.persistence.EntityManager;
import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.application.query.dto.GetAllNotCompletedOrdersDto;
import microarch.delivery.core.domain.model.order.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class GetAllNotCompletedOrdersQueryHandlerImpl implements GetAllNotCompletedOrdersQueryHandler {

    private static final String QUERY = """
            select o
            from Order o
            where o.status in (:statuses)
            """;
    private static final Set<OrderStatus> STATUSES = Set.of(OrderStatus.CREATED, OrderStatus.ASSIGNED);

    private final EntityManager entityManager;

    @Override
    public Result<GetAllNotCompletedOrdersQueryResponse, Error> handle(GetAllNotCompletedOrdersQuery query) {
        var orders = entityManager.createQuery(QUERY, GetAllNotCompletedOrdersDto.class)
                .setParameter("statuses", STATUSES).getResultList();
        return Result.success(new GetAllNotCompletedOrdersQueryResponse(orders));
    }
}
