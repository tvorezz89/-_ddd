package microarch.delivery.core.application.command;

import libs.errs.Error;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateOrderCommandHandlerImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private CreateOrderCommandHandlerImpl handler;


    @DisplayName("Создание заказа: успех")
    @Test
    void handle_shouldSuccess_whenCommandIsValid() {
        var command = CreateOrderCommand.create(
                UUID.randomUUID(),
                "Russia",
                "Moscow",
                "Lenina",
                "10",
                "5",
                5
        ).getValue();

        UnitResult<Error> result = handler.handle(command);

        assertTrue(result.isSuccess());
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
