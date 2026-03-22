package microarch.delivery.core.application.command;

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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCourierCommandHandlerImplTest {

    @Mock
    private CourierRepository courierRepository;

    @InjectMocks
    private CreateCourierCommandHandlerImpl handler;

    @DisplayName("Создание курьера: успех")
    @Test
    void handle_shouldSuccess_whenCommandIsValid() {
        var command = CreateCourierCommand.create("Ivan", 10);
        when(courierRepository.save(any())).thenReturn(Courier.mustCreate("вася", Speed.mustCreate(10), Location.mustCreate(4, 5)));

        Result<UUID, Error> result = handler.handle(command.getValue());

        assertTrue(result.isSuccess());
        verify(courierRepository, times(1)).save(any());
    }

}
