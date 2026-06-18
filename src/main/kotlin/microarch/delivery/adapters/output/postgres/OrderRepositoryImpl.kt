package microarch.delivery.adapters.output.postgres

import microarch.delivery.core.domain.model.order.Order
import microarch.delivery.core.domain.model.order.Status
import microarch.delivery.core.ports.OrderRepository
import org.springframework.stereotype.Repository
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Repository
class OrderRepositoryImpl(private val jpaRepository: OrderJpaRepository) : OrderRepository {

    override fun save(order: Order) {
        jpaRepository.save(order)
    }

    override fun getById(id: UUID): Order? = jpaRepository.findById(id).getOrNull()

    override fun getAnyCreated(): Order? = jpaRepository.findFirstByStatus(Status.CREATED).getOrNull()

    override fun getAllAssigned(): Collection<Order> = jpaRepository.findAllByStatus(Status.ASSIGNED)
}
