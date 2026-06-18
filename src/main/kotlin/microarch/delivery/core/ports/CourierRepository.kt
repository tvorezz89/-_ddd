package microarch.delivery.core.ports

import microarch.delivery.core.domain.model.courier.Courier
import java.util.UUID

interface CourierRepository {
    fun save(courier: Courier)
    fun getById(id: UUID): Courier?
    fun getAll(): Collection<Courier>
}
