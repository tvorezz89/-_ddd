package microarch.delivery.core.domain.model.courier

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import libs.ddd.BaseEntity
import libs.errs.Error
import libs.errs.GeneralErrors
import microarch.delivery.core.domain.model.kernel.Location
import java.util.UUID

class Assignment private constructor(val orderId: UUID, val volume: Volume, val location: Location, status: Status) :
    BaseEntity<UUID>(id = UUID.randomUUID()) {

    var status: Status = status
        private set

    fun complete(location: Location): Either<Error, Assignment> {
        val distance = this.location.distance(location)
        if (distance > MAX_DISTANCE) return currentLocationTooFarFromTarget().left()

        this.status = Status.COMPLETED
        return this.right()
    }

    companion object {
        fun of(orderId: UUID, volume: Volume, location: Location, status: Status): Either<Error, Assignment> {
            if (status != Status.ASSIGNED) return GeneralErrors.valueIsInvalid("status", status).left()

            val assignment = Assignment(orderId = orderId, volume = volume, location = location, status = status)
            return assignment.right()
        }

        private fun currentLocationTooFarFromTarget(): Error = Error.of(
            "current.location.too.far.from.target",
            "The current location must be within one cell or less from the target."
        )

        const val MAX_DISTANCE = 1
    }
}

enum class Status {
    ASSIGNED,
    COMPLETED
}
