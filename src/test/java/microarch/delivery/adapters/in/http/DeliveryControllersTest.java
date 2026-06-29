package microarch.delivery.adapters.in.http;

import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import microarch.delivery.adapters.in.http.model.Address;
import microarch.delivery.adapters.in.http.model.NewCourier;
import microarch.delivery.adapters.in.http.model.NewOrder;
import microarch.delivery.core.application.CompleteOrderCommand;
import microarch.delivery.core.application.CompleteOrderCommandHandler;
import microarch.delivery.core.application.CreateCourierCommand;
import microarch.delivery.core.application.CreateCourierCommandHandler;
import microarch.delivery.core.application.CreateOrderCommand;
import microarch.delivery.core.application.CreateOrderCommandHandler;
import microarch.delivery.core.application.GetAllCouriersQuery;
import microarch.delivery.core.application.GetAllCouriersQueryHandler;
import microarch.delivery.core.application.GetAllCouriersResponse;
import microarch.delivery.core.application.GetNotCompletedOrdersQuery;
import microarch.delivery.core.application.GetNotCompletedOrdersQueryHandler;
import microarch.delivery.core.application.GetNotCompletedOrdersResponse;
import microarch.delivery.core.application.MoveCourierCommand;
import microarch.delivery.core.application.MoveCourierCommandHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DeliveryControllersTest {

    @Test
    void createOrderShouldDelegateToUseCaseAndReturnCreatedOrderId() {
        var handler = new RecordingCreateOrderHandler();
        var controller = new CreateOrderController(handler);
        var orderId = UUID.randomUUID();
        var request = new NewOrder(orderId, new Address("Russia", "Moscow", "Tverskaya", "1", "2"));
        request.setVolume(3);

        var response = controller.createOrder(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getOrderId()).isEqualTo(orderId);
        assertThat(handler.command.getOrderId()).isEqualTo(orderId);
        assertThat(handler.command.getCountry()).isEqualTo("Russia");
        assertThat(handler.command.getCity()).isEqualTo("Moscow");
        assertThat(handler.command.getStreet()).isEqualTo("Tverskaya");
        assertThat(handler.command.getHouse()).isEqualTo("1");
        assertThat(handler.command.getApartment()).isEqualTo("2");
        assertThat(handler.command.getVolume().getValue()).isEqualTo(3);
    }

    @Test
    void createCourierShouldDelegateToUseCaseAndReturnCreatedCourierId() {
        var courierId = UUID.randomUUID();
        var handler = new RecordingCreateCourierHandler(courierId);
        var controller = new CreateCourierController(handler);

        var response = controller.createCourier(new NewCourier("Ivan"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getCourierId()).isEqualTo(courierId);
        assertThat(handler.command.getName()).isEqualTo("Ivan");
    }

    @Test
    void moveCourierShouldMapLocationAndDelegateToUseCase() {
        var handler = new RecordingMoveCourierHandler();
        var controller = new MoveCourierController(handler);
        var courierId = UUID.randomUUID();

        var response = controller.moveCourier(courierId, new microarch.delivery.adapters.in.http.model.Location(4, 5));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(handler.command.getCourierId()).isEqualTo(courierId);
        assertThat(handler.command.getLocation().getX()).isEqualTo(4);
        assertThat(handler.command.getLocation().getY()).isEqualTo(5);
    }

    @Test
    void completeOrderShouldDelegateToUseCase() {
        var handler = new RecordingCompleteOrderHandler();
        var controller = new CompleteOrderController(handler);
        var courierId = UUID.randomUUID();
        var orderId = UUID.randomUUID();

        var response = controller.completeOrder(courierId, orderId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(handler.command.getCourierId()).isEqualTo(courierId);
        assertThat(handler.command.getOrderId()).isEqualTo(orderId);
    }

    @Test
    void getCouriersShouldDelegateToQueryAndMapResponse() {
        var courierId = UUID.randomUUID();
        var handler = new StubGetAllCouriersHandler(
                new GetAllCouriersResponse(List.of(new GetAllCouriersResponse.CourierDto(courierId, "Ivan",
                        microarch.delivery.core.domain.model.Location.mustCreate(6, 7)))));
        var controller = new GetCouriersController(handler);

        var response = controller.getCouriers();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(handler.called).isTrue();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst().getId()).isEqualTo(courierId);
        assertThat(response.getBody().getFirst().getName()).isEqualTo("Ivan");
        assertThat(response.getBody().getFirst().getLocation().getX()).isEqualTo(6);
        assertThat(response.getBody().getFirst().getLocation().getY()).isEqualTo(7);
    }

    @Test
    void getOrdersShouldDelegateToQueryAndMapResponse() {
        var orderId = UUID.randomUUID();
        var handler = new StubGetNotCompletedOrdersHandler(
                new GetNotCompletedOrdersResponse(List.of(new GetNotCompletedOrdersResponse.OrderDto(orderId,
                        microarch.delivery.core.domain.model.Location.mustCreate(8, 9)))));
        var controller = new GetOrdersController(handler);

        var response = controller.getOrders();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(handler.called).isTrue();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst().getId()).isEqualTo(orderId);
        assertThat(response.getBody().getFirst().getLocation().getX()).isEqualTo(8);
        assertThat(response.getBody().getFirst().getLocation().getY()).isEqualTo(9);
    }

    private static final class RecordingCreateOrderHandler implements CreateOrderCommandHandler {
        private CreateOrderCommand command;

        @Override
        public UnitResult<Error> handle(CreateOrderCommand command) {
            this.command = command;
            return UnitResult.success();
        }
    }

    private static final class RecordingCreateCourierHandler implements CreateCourierCommandHandler {
        private final UUID courierId;
        private CreateCourierCommand command;

        private RecordingCreateCourierHandler(UUID courierId) {
            this.courierId = courierId;
        }

        @Override
        public Result<UUID, Error> handle(CreateCourierCommand command) {
            this.command = command;
            return Result.success(courierId);
        }
    }

    private static final class RecordingMoveCourierHandler implements MoveCourierCommandHandler {
        private MoveCourierCommand command;

        @Override
        public UnitResult<Error> handle(MoveCourierCommand command) {
            this.command = command;
            return UnitResult.success();
        }
    }

    private static final class RecordingCompleteOrderHandler implements CompleteOrderCommandHandler {
        private CompleteOrderCommand command;

        @Override
        public UnitResult<Error> handle(CompleteOrderCommand command) {
            this.command = command;
            return UnitResult.success();
        }
    }

    private static final class StubGetAllCouriersHandler implements GetAllCouriersQueryHandler {
        private final GetAllCouriersResponse response;
        private boolean called;

        private StubGetAllCouriersHandler(GetAllCouriersResponse response) {
            this.response = response;
        }

        @Override
        public GetAllCouriersResponse handle(GetAllCouriersQuery query) {
            called = true;
            return response;
        }
    }

    private static final class StubGetNotCompletedOrdersHandler implements GetNotCompletedOrdersQueryHandler {
        private final GetNotCompletedOrdersResponse response;
        private boolean called;

        private StubGetNotCompletedOrdersHandler(GetNotCompletedOrdersResponse response) {
            this.response = response;
        }

        @Override
        public GetNotCompletedOrdersResponse handle(GetNotCompletedOrdersQuery query) {
            called = true;
            return response;
        }
    }
}
