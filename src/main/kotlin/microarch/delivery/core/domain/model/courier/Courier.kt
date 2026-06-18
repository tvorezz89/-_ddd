package microarch.delivery.core.domain.model.courier

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.right
import libs.ddd.Aggregate
import libs.errs.Error
import libs.errs.GeneralErrors
import microarch.delivery.core.domain.model.kernel.Location
import java.math.BigDecimal
import java.util.UUID

class Courier private constructor(val name: String, location: Location, val maxVolume: Volume) : Aggregate<UUID>(id = UUID.randomUUID()) {

    var location: Location = location
        private set

    var assignments: List<Assignment> = emptyList()
        private set

    fun move(location: Location): Either<Error, Unit> = either {
        ensure(this@Courier.location != location) { anotherLocationRequiredError() }
        this@Courier.location = location
    }

    fun completeAssignment(assignmentId: UUID): Either<Error, Unit> = either {
        val assignment = this@Courier.assignments.firstOrNull { it.getId() == assignmentId }
        ensure(assignment != null) { notLetAssignedError() }

        assignment.complete(location).bind()

        this@Courier.assignments = assignments.filter { it != assignment }
    }

    fun assign(orderId: UUID, volume: Volume, location: Location): Either<Error, Assignment> = either {
        val assignedOrderIds = this@Courier.assignments.map { it.orderId }
        ensure(!assignedOrderIds.contains(orderId)) { alreadyAssignedError() }
        ensure(this@Courier.hasEnoughVolume(volume)) { volumeLimitExceededError() }

        val assignment = Assignment.of(
            orderId = orderId,
            volume = volume,
            location = location,
            status = AssignmentStatus.ASSIGNED
        ).bind()

        this@Courier.assignments += assignment
        assignment
    }

    fun hasEnoughVolume(volume: Volume): Boolean {
        val totalVolume = assignments.map { it.volume } + volume
        val volumeSum = totalVolume.reduce { acc, volume -> acc + volume }
        return volumeSum <= this.maxVolume
    }

    companion object {
        fun of(name: String, location: Location): Either<Error, Courier> {
            if (name.isEmpty()) return GeneralErrors.valueIsRequired("name").left()

            return maxVolume.fold(
                ifLeft = { GeneralErrors.valueIsInvalid("maxVolume", maxVolume).left() },
                ifRight = { Courier(name, location, it).right() }
            )
        }

        private val maxVolume: Either<Error, Volume> = Volume.of(BigDecimal("20.0"))

        private fun anotherLocationRequiredError(): Error = Error.of(
            "another.location.required",
            "Another location from current required to move courier."
        )

        private fun notLetAssignedError(): Error = Error.of(
            "not.let.assigned",
            "This assignment not let assigned to this courier."
        )

        private fun alreadyAssignedError(): Error = Error.of(
            "already.assigned",
            "This assignment has been already assigned to this courier."
        )

        private fun volumeLimitExceededError(): Error = Error.of(
            "volume.limit.exceeded",
            "Volume limit exceeded for this courier. The maximum allowable volume is 20 liters"
        )
    }
}
