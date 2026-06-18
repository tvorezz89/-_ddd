package microarch.delivery.core.domain.fixture

import microarch.delivery.core.domain.model.courier.Courier
import microarch.delivery.core.domain.model.courier.Volume
import microarch.delivery.core.domain.model.kernel.Location
import microarch.delivery.core.domain.model.order.Order
import java.math.BigDecimal
import java.util.UUID

object DataFixtures {

    fun createCourier(name: String = "courier") = Courier.of(
        name = name,
        location = Location.of(x = 1, y = 5).getOrNull()!!
    ).getOrNull()!!

    fun createOrder(orderId: UUID = UUID.randomUUID()) = Order.of(
        id = orderId,
        volume = Volume.of(BigDecimal.ONE).getOrNull()!!,
        location = Location.of(x = 5, y = 5).getOrNull()!!
    ).getOrNull()!!
}
