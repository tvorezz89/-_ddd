package microarch.delivery.adapters.out.postgres;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface CourierJpaRepository extends JpaRepository<CourierJpaEntity, UUID> {
}
