package microarch.delivery.core.ports;

import queues.order.OrderEventsProto;

public interface OrderCompleteProducer {

    void publish(OrderEventsProto.OrderCompletedIntegrationEvent event);

}
