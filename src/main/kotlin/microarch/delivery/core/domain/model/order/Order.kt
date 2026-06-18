package microarch.delivery.core.domain.model.order

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import libs.ddd.Aggregate
import libs.errs.Error
import microarch.delivery.core.domain.model.courier.Volume
import microarch.delivery.core.domain.model.kernel.Location
import java.util.UUID

@Entity
@Table(name = "orders")
class Order private constructor(id: UUID, location: Location, volume: Volume, status: Status) : Aggregate<UUID>(id = id) {

    @Embedded
    val location: Location = location

    @Embedded
    val volume: Volume = volume

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: Status = status
        private set

    fun assign(): Either<Error, Unit> {
        if (status != Status.CREATED) return canAssignOnlyCreatedOrderError().left()

        status = Status.ASSIGNED
        return Unit.right()
    }

    fun complete(): Either<Error, Unit> {
        if (status != Status.ASSIGNED) return canCompleteOnlyAssignedOrderError().left()

        status = Status.COMPLETED
        return Unit.right()
    }

    companion object {
        fun of(id: UUID, volume: Volume, location: Location): Either<Error, Order> = Order(id, location, volume, Status.CREATED).right()

        private fun canAssignOnlyCreatedOrderError(): Error = Error.of(
            "can.assign.only.created.order",
            "Order can be assigned only from Created status."
        )

        private fun canCompleteOnlyAssignedOrderError(): Error = Error.of(
            "can.complete.only.assigned.order",
            "Order can be completed only from Assigned status."
        )
    }
}
