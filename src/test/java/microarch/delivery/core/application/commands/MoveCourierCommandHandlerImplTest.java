package microarch.delivery.core.application.commands;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MoveCourierCommandHandlerImplTest {

    private final CourierRepository courierRepository = mock(CourierRepository.class);
    private final MoveCourierCommandHandlerImpl handler = new MoveCourierCommandHandlerImpl(courierRepository);

    private static final Location LOCATION = Location.create(5, 5).getValue();
    private static final Location NEW_LOCATION = Location.create(3, 3).getValue();

    @Test
    void handle_shouldMoveCourierAndSave() {
        var courierId = UUID.randomUUID();
        var courier = Courier.create("Ivan", LOCATION).getValue();
        when(courierRepository.findById(courierId)).thenReturn(Optional.of(courier));

        var command = MoveCourierCommand.create(courierId, NEW_LOCATION).getValue();
        var result = handler.handle(command);

        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.getLocation()).isEqualTo(NEW_LOCATION);
        verify(courierRepository).save(courier);
    }

    @Test
    void handle_shouldFail_whenCourierNotFound() {
        var courierId = UUID.randomUUID();
        when(courierRepository.findById(courierId)).thenReturn(Optional.empty());

        var command = MoveCourierCommand.create(courierId, NEW_LOCATION).getValue();
        var result = handler.handle(command);

        assertThat(result.isFailure()).isTrue();
    }
}
