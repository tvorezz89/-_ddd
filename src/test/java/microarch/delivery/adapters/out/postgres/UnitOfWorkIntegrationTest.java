package microarch.delivery.adapters.out.postgres;

import microarch.delivery.core.application.UnitOfWork;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Проверяет, что разные агрегаты сохраняются транзакционно: при сбое в середине блока Unit of Work ни один агрегат не
 * сохраняется.
 */
@SpringBootTest
class UnitOfWorkIntegrationTest extends PostgresIntegrationTestBase {

    @Autowired
    UnitOfWork unitOfWork;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CourierRepository courierRepository;

    @Autowired
    OrderJpaRepository orderJpa;

    @Autowired
    CourierJpaRepository courierJpa;

    @BeforeEach
    void cleanUp() {
        courierJpa.deleteAll();
        orderJpa.deleteAll();
    }

    @Test
    void savesBothAggregatesWhenNoError() {
        // Act
        var courierName = "Иван-" + UUID.randomUUID();
        var orderId = unitOfWork.execute(() -> {
            var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(1, 1), Volume.mustCreate(5));
            orderRepository.add(order);
            courierRepository.add(Courier.mustCreate(courierName, Location.mustCreate(2, 2)));
            return order.getId();
        });

        // Assert
        assertThat(orderRepository.findById(orderId)).isPresent();
        assertThat(courierRepository.getAll()).anyMatch(c -> c.getName().equals(courierName));
    }

    @Test
    void rollsBackBothAggregatesOnError() {
        // Arrange
        var orderId = UUID.randomUUID();
        var courierName = "Откат-" + orderId;

        // Act
        assertThatThrownBy(() -> unitOfWork.execute(() -> {
            orderRepository.add(Order.mustCreate(orderId, Location.mustCreate(1, 1), Volume.mustCreate(5)));
            courierRepository.add(Courier.mustCreate(courierName, Location.mustCreate(2, 2)));
            throw new IllegalStateException("boom");
        })).isInstanceOf(IllegalStateException.class);

        // Assert — ни заказ, ни курьер не должны сохраниться
        assertThat(orderRepository.findById(orderId)).isEmpty();
        assertThat(courierRepository.getAll()).noneMatch(c -> c.getName().equals(courierName));
    }
}
