package microarch.delivery.core.ports;

import queues.order.OrderEventsProto;

public interface OrderCreateProducer {

    void publish(OrderEventsProto.OrderCreatedIntegrationEvent event);

}
