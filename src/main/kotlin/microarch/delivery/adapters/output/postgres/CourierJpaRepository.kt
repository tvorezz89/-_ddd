package microarch.delivery.adapters.output.postgres

import microarch.delivery.core.domain.model.courier.Courier
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CourierJpaRepository : JpaRepository<Courier, UUID>
