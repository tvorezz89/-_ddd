package microarch.delivery.core.domain.service;

import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.Location;
import microarch.delivery.core.domain.Speed;
import microarch.delivery.core.domain.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static microarch.delivery.core.domain.service.OrderDispatcherDomainServiceImpl.Errors.noCouriersCanDispatchOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderDispatcherDomainServiceImplTest {

    @InjectMocks
    OrderDispatcherDomainServiceImpl service;

    @Mock
    AssignOrderDomainService assignOrderDomainService;

    private final String courierName = "Alice";
    private final Speed speed = Speed.mustCreate(2);

    @Test
    void dispatchToCourierSuccess() {
        var courier1 = Courier.mustCreate(courierName, speed, Location.mustCreate(1, 2));
        var courier2 = Courier.mustCreate(courierName, speed, Location.mustCreate(3, 1));
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(1, 1), Volume.mustCreate(2));

        when(assignOrderDomainService.assignOrder(any(), any())).thenReturn(UnitResult.success());

        Result<Courier, Error> result = service.dispatch(order, List.of(courier1, courier2));


        assertTrue(result.isSuccess());
        assertEquals(result.getValue(), courier1);
    }


    @Test
    void dispatchToCourierFailedForEmptyList() {
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(1, 1), Volume.mustCreate(2));

        Result<Courier, Error> result = service.dispatch(order, List.of());

        assertTrue(result.isFailure());
        assertEquals(result.getError(), noCouriersCanDispatchOrder() );
    }

    @Test
    void dispatchToCourierFailedOrderIsAlreadyAssigned() {
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(1, 1), Volume.mustCreate(2));
        var courier = Courier.mustCreate(courierName, speed, Location.mustCreate(3, 1));

        order.assignToCourier(UUID.randomUUID());

        Result<Courier, Error> result = service.dispatch(order, List.of(courier));

        assertTrue(result.isFailure());
        assertEquals(result.getError(), Order.Errors.canOnlDispatchCreatedOrder() );

    }

    @Test
    void dispatchToCourierFailedOrderIsVeryBig() {
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(1, 1), Volume.mustCreate(1000));
        var courier = Courier.mustCreate(courierName, speed, Location.mustCreate(3, 1));

        Result<Courier, Error> result = service.dispatch(order, List.of(courier));

        assertTrue(result.isFailure());
        assertEquals(result.getError(), noCouriersCanDispatchOrder() );

    }

    @Test
    void dispatchToCourierFailedForAssigning() {

        var courier1 = Courier.mustCreate(courierName, speed, Location.mustCreate(1, 2));
        var courier2 = Courier.mustCreate(courierName, speed, Location.mustCreate(3, 1));
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(1, 1), Volume.mustCreate(2));


        when(assignOrderDomainService.assignOrder(any(), any())).thenReturn(UnitResult.failure(Courier.Errors.couldNotTakeOrder()));

        Result<Courier, Error> result = service.dispatch(order, List.of(courier1, courier2));

        assertTrue(result.isFailure());
    }


}
