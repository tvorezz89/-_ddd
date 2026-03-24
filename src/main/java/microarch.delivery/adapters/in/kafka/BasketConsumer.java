package microarch.delivery.adapters.in.kafka;

import com.google.protobuf.util.JsonFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microarch.delivery.core.application.command.CreateOrderCommand;
import microarch.delivery.core.application.command.CreateOrderCommandHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import queues.basket.BasketEventsProto;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasketConsumer {

    private final CreateOrderCommandHandler handler;

    @KafkaListener(topics = "${app.kafka.baskets-events-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        log.info("Received message: {}", message);

        try {
            var eventBuilder = BasketEventsProto.BasketConfirmedIntegrationEvent.newBuilder();
            JsonFormat.parser().merge(message, eventBuilder);

            var event = eventBuilder.build();

            var command = CreateOrderCommand.create(UUID.randomUUID(), event.getAddress().getCountry(),
                    event.getAddress().getCity(), event.getAddress().getStreet(), event.getAddress().getHouse(),
                    event.getAddress().getApartment(), event.getVolume());

            if (command.isFailure()) {
                log.error("Invalid message: {}", message);
            }

            var result = handler.handle(command.getValue());
            if (result.isFailure()) {
                log.error("Error during processing message: {}", message);
            }
        } catch (Exception ex) {
            log.error("Error during process message: {}", message);
        }
    }

}
