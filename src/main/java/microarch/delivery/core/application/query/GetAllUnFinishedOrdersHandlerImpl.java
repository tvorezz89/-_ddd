package microarch.delivery.core.application.query;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.application.query.dto.LocationDto;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllUnFinishedOrdersHandlerImpl implements GetAllUnFinishedOrdersHandler {

    private final OrderRepository orderRepository;

    @Override
    public Result<GetAllUnFinishedOrdersResponseDto, Error> handle() {
        var orders = orderRepository.findAllUnfinished();
        return Result.success(mapToResponse(orders));
    }

    private GetAllUnFinishedOrdersResponseDto mapToResponse(List<Order> orders) {
        return new GetAllUnFinishedOrdersResponseDto(orders.stream().map(this::toDto).toList(), null);
    }

    private GetAllUnFinishedOrdersResponseDto.OrderResponse toDto(Order order) {
        return new GetAllUnFinishedOrdersResponseDto.OrderResponse(
                order.getId(),
                new LocationDto(order.getLocation().getX(), order.getLocation().getY())
        );
    }

}
