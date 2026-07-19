package microarch.delivery.core.application;

import microarch.delivery.core.domain.model.courier.AssignmentStatus;
import microarch.delivery.core.domain.model.order.Location;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.UnitOfWork;
import org.springframework.stereotype.Service;

@Service
public class MoveCouriersUseCase {
    private final CourierRepository couriers;
    private final UnitOfWork unitOfWork;

    public MoveCouriersUseCase(CourierRepository couriers1, UnitOfWork unitOfWork1) {
        couriers = couriers1;
        unitOfWork = unitOfWork1;
    }

    public void handle() {
        unitOfWork.execute(() -> couriers.getAll()
                .forEach(courier -> courier.getAssignments().stream()
                        .filter(assignment -> assignment.getStatus() == AssignmentStatus.ASSIGNED).findFirst()
                        .ifPresent(assignment -> {
                            var target = assignment.getLocation();
                            var current = courier.getLocation();
                            courier.move(Location.of(moveCoordinate(current.getX(), target.getX()),
                                    moveCoordinate(current.getY(), target.getY())));
                            couriers.update(courier);
                        })));
    }

    private int moveCoordinate(int current, int target) {
        return current + Integer.compare(target, current);
    }
}
