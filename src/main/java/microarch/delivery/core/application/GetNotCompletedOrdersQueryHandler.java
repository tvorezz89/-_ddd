package microarch.delivery.core.application;

public interface GetNotCompletedOrdersQueryHandler {
    GetNotCompletedOrdersResponse handle(GetNotCompletedOrdersQuery query);
}
