package microarch.delivery.adapters.out.postgres;

import microarch.delivery.core.domain.model.courier.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CourierJpaRepository extends JpaRepository<Courier, UUID> {

    @Query(value = """
            select *
            from couriers c
            where exists(
                select 1
                from storage_places sp
                where sp.order_id is null
                    and sp.courier_id = c.id)
            """, nativeQuery = true)
    List<Courier> findAllFreeCouriers();
}
