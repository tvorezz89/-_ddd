package microarch.delivery.core.application.commands;

import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CreateOrderCommandHandlerImplTest {

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final CreateOrderCommandHandlerImpl handler = new CreateOrderCommandHandlerImpl(orderRepository);

    @Test
    void handle_shouldCreateOrderAndSave() {
        var command = CreateOrderCommand.create(UUID.randomUUID(), "Russia", "Main St", "Moscow", "1", "42", 5)
                .getValue();

        var result = handler.handle(command);

        assertThat(result.isSuccess()).isTrue();
        verify(orderRepository).save(any());
    }
}
