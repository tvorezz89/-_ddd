package microarch.delivery.core.ports

import microarch.delivery.core.domain.model.order.Order
import java.util.UUID

interface OrderRepository {
    fun save(order: Order)
    fun getById(id: UUID): Order?
    fun getAnyCreated(): Order?
    fun getAllAssigned(): Collection<Order>
}
