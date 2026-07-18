package microarch.delivery.core.application;

import microarch.delivery.core.domain.service.OrderDispatchService;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import microarch.delivery.core.ports.UnitOfWork;
import org.springframework.stereotype.Service;

@Service
public class AssignOrderUseCase {
    private final OrderRepository orders;
    private final CourierRepository couriers;
    private final OrderDispatchService orderDispatchService;
    private final UnitOfWork unitOfWork;

    public AssignOrderUseCase(OrderRepository orders1, CourierRepository couriers1,
            OrderDispatchService orderDispatchService1, UnitOfWork unitOfWork1) {
        orders = orders1;
        couriers = couriers1;
        orderDispatchService = orderDispatchService1;
        unitOfWork = unitOfWork1;
    }

    public void handle() {
        unitOfWork.execute(() -> orders.getAnyCreated().ifPresent(order -> {
            var allCouriers = couriers.getAll();
            var result = orderDispatchService.dispatch(order, allCouriers);
            if (result.isSuccess()) {
                couriers.update(result.getValue());
                orders.update(order);
            }
        }));
    }
}
