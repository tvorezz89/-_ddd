package microarch.delivery.adapters.output.postgres

import microarch.delivery.core.domain.model.order.Order
import microarch.delivery.core.domain.model.order.Status
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface OrderJpaRepository : JpaRepository<Order, UUID> {
    fun findFirstByStatus(status: Status): Optional<Order>
    fun findAllByStatus(status: Status): Collection<Order>
}
