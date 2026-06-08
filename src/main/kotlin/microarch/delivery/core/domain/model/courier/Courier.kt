package microarch.delivery.core.domain.model.courier

import arrow.core.Either
import arrow.core.left
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

    fun move(location: Location): Either<Error, Unit> {
        if (this.location == location) return anotherLocationRequiredError().left()
        this.location = location
        return Unit.right()
    }

    fun completeAssignment(assignment: Assignment): Either<Error, Unit> {
        if (!assignments.contains(assignment)) return notLetAssignedError().left()

        val completeStatus = assignment.complete(location)
        if (completeStatus.isLeft()) return currentLocationTooFarFromTargetError().left()

        this.assignments = assignments.filter { it != assignment }
        return Unit.right()
    }

    fun assign(assignment: Assignment): Either<Error, Unit> {
        if (assignments.contains(assignment)) return alreadyAssignedError().left()

        val totalAssignments = assignments + assignment
        val totalVolume = totalAssignments.map { it.volume }.reduce { acc, volume -> acc + volume }

        if (totalVolume > this.maxVolume) return volumeLimitExceededError().left()

        assignments = assignments + assignment
        return Unit.right()
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

        private fun currentLocationTooFarFromTargetError(): Error = Error.of(
            "current.location.too.far.from.target",
            "The current location must be within one cell or less from the target."
        )

        private fun volumeLimitExceededError(): Error = Error.of(
            "volume.limit.exceeded",
            "Volume limit exceeded for this courier. The maximum allowable volume is 20 liters"
        )
    }
}
