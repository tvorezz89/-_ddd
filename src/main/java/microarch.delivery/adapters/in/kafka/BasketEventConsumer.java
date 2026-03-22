package microarch.delivery.adapters.in.kafka;

import lombok.RequiredArgsConstructor;
import microarch.delivery.core.application.command.CreateOrderCommand;
import microarch.delivery.core.application.command.CreateOrderCommandHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import queues.basket.BasketEventsProto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasketEventConsumer {

    private final CreateOrderCommandHandler createOrderCommandHandler;

    @KafkaListener(topics = "${app.kafka.baskets-events-topic}")
    public void listen(byte[] message) {
        try {
            var event = BasketEventsProto.BasketConfirmedIntegrationEvent.parseFrom(message);

            var createOrderResult = CreateOrderCommand.create(UUID.fromString(event.getBasketId()),
                    event.getAddress().getCountry(),
                    event.getAddress().getCity(),
                    event.getAddress().getStreet(),
                    event.getAddress().getHouse(),
                    event.getAddress().getApartment(),
                    event.getVolume()
            );
            if (createOrderResult.isFailure()) {
                throw new RuntimeException("Cant create order");
            }

            var handleCommandResult = createOrderCommandHandler.handle(createOrderResult.getValue());
            if (handleCommandResult.isFailure()) {
                throw new RuntimeException("Failed to handle command: " + handleCommandResult.getError());
            }
        } catch (com.google.protobuf.InvalidProtocolBufferException ex) {
            throw new RuntimeException("Failed to parse protobuf message", ex);
        }
    }
}
