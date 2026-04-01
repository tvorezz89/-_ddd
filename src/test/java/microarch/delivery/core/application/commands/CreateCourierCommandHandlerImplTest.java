package microarch.delivery.core.application.commands;

import microarch.delivery.core.ports.CourierRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CreateCourierCommandHandlerImplTest {

    private final CourierRepository courierRepository = mock(CourierRepository.class);
    private final CreateCourierCommandHandlerImpl handler = new CreateCourierCommandHandlerImpl(courierRepository);

    @Test
    void handle_shouldCreateCourierAndSave() {
        var command = CreateCourierCommand.create("Ivan").getValue();

        var result = handler.handle(command);

        assertThat(result.isSuccess()).isTrue();
        verify(courierRepository).save(any());
    }
}
