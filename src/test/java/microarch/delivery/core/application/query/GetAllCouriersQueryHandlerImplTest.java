package microarch.delivery.core.application.query;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.Location;
import microarch.delivery.core.domain.Speed;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllCouriersQueryHandlerImplTest {

    @Mock
    private CourierRepository courierRepository;

    @InjectMocks
    private GetAllCouriersQueryHandlerImpl handler;

    @DisplayName("Получение всех курьеров: успех")
    @Test
    void handle_shouldReturnCouriersList_whenCouriersExist() {
        Courier courier = Courier.mustCreate("Ivan", Speed.mustCreate(10), Location.mustCreate(1, 1));
        
        when(courierRepository.findAll()).thenReturn(List.of(courier));

        Result<GetAllCouriersResponseDto, Error> result = handler.handle();

        assertTrue(result.isSuccess());
        assertNotNull(result.getValue());
        assertEquals(1, result.getValue().couriers().size());
        assertEquals("Ivan", result.getValue().couriers().getFirst().getName());
    }

    @DisplayName("Получение всех курьеров: успех с пустым списком")
    @Test
    void handle_shouldReturnEmptyList_whenNoCouriers() {
        when(courierRepository.findAll()).thenReturn(List.of());

        Result<GetAllCouriersResponseDto, Error> result = handler.handle();

        assertTrue(result.isSuccess());
        assertNotNull(result.getValue());
        assertTrue(result.getValue().couriers().isEmpty());
    }
}
