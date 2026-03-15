package microarch.delivery.core.application.command;

import libs.errs.Error;
import libs.errs.UnitResult;
import microarch.delivery.core.ports.CourierRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

        UnitResult<Error> result = handler.handle(command.getValue());

        assertTrue(result.isSuccess());
        verify(courierRepository, times(1)).save(any());
    }

}
