package microarch.delivery.adapters.output.postgres

import microarch.delivery.core.domain.model.courier.Courier
import microarch.delivery.core.ports.CourierRepository
import org.springframework.stereotype.Repository
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Repository
class CourierRepositoryImpl(private val jpaRepository: CourierJpaRepository) : CourierRepository {

    override fun save(courier: Courier) {
        jpaRepository.save(courier)
    }

    override fun getById(id: UUID): Courier? = jpaRepository.findById(id).getOrNull()

    override fun getAll(): Collection<Courier> = jpaRepository.findAll()
}
