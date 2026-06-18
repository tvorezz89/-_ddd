package microarch.delivery.core.domain.model.courier

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import libs.ddd.BaseEntity
import libs.errs.Error
import libs.errs.GeneralErrors
import microarch.delivery.core.domain.model.kernel.Location
import java.util.UUID

@Entity
@Table(name = "assignment")
class Assignment private constructor(orderId: UUID, volume: Volume, location: Location, status: AssignmentStatus) :
    BaseEntity<UUID>(id = UUID.randomUUID()) {

    @Column(name = "order_id")
    val orderId: UUID = orderId

    @Embedded
    val volume: Volume = volume

    @Embedded
    val location: Location = location

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: AssignmentStatus = status
        private set

    fun complete(location: Location): Either<Error, Unit> {
        val distance = this.location.distance(location)
        if (distance > MAX_DISTANCE) return currentLocationTooFarFromTargetError().left()

        this.status = AssignmentStatus.COMPLETED
        return Unit.right()
    }

    companion object {
        fun of(orderId: UUID, volume: Volume, location: Location, status: AssignmentStatus): Either<Error, Assignment> {
            if (status != AssignmentStatus.ASSIGNED) return GeneralErrors.valueIsInvalid("status", status).left()

            val assignment = Assignment(orderId = orderId, volume = volume, location = location, status = status)
            return assignment.right()
        }

        private fun currentLocationTooFarFromTargetError(): Error = Error.of(
            "current.location.too.far.from.target",
            "The current location must be within one cell or less from the target."
        )

        const val MAX_DISTANCE = 1
    }
}

enum class AssignmentStatus {
    ASSIGNED,
    COMPLETED
}
