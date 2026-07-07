package microarch.delivery.adapters.in.kafka;

import libs.errs.Error;
import libs.errs.UnitResult;
import microarch.delivery.core.application.CreateOrderCommand;
import microarch.delivery.core.application.CreateOrderCommandHandler;
import org.junit.jupiter.api.Test;
import queues.basket.events.BasketEventsProto;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BasketEventsConsumerTest {

    @Test
    void listenShouldCreateOrderFromBasketConfirmedEvent() {
        var handler = new RecordingCreateOrderHandler();
        var consumer = new BasketEventsConsumer(handler);
        var basketId = UUID.randomUUID();
        var event = BasketEventsProto.BasketConfirmedIntegrationEvent.newBuilder().setBasketId(basketId.toString())
                .setAddress(BasketEventsProto.Address.newBuilder().setCountry("Russia").setCity("Moscow")
                        .setStreet("Tverskaya").setHouse("1").setApartment("2").build())
                .setVolume(3).build();

        consumer.listen(event.toByteArray());

        assertThat(handler.calls).isEqualTo(1);
        assertThat(handler.command.getOrderId()).isEqualTo(basketId);
        assertThat(handler.command.getCountry()).isEqualTo("Russia");
        assertThat(handler.command.getCity()).isEqualTo("Moscow");
        assertThat(handler.command.getStreet()).isEqualTo("Tverskaya");
        assertThat(handler.command.getHouse()).isEqualTo("1");
        assertThat(handler.command.getApartment()).isEqualTo("2");
        assertThat(handler.command.getVolume().getValue()).isEqualTo(3);
    }

    private static final class RecordingCreateOrderHandler implements CreateOrderCommandHandler {
        private int calls;
        private CreateOrderCommand command;

        @Override
        public UnitResult<Error> handle(CreateOrderCommand command) {
            calls++;
            this.command = command;
            return UnitResult.success();
        }
    }
}
