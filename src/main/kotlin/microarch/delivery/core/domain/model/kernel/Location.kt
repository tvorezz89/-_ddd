package microarch.delivery.core.domain.model.kernel

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import libs.ddd.ValueObject
import libs.errs.Error
import libs.errs.GeneralErrors
import kotlin.math.abs

@Embeddable
class Location private constructor(@Column(name = "location_x") val x: Int, @Column(name = "location_y") val y: Int) :
    ValueObject<Location>() {

    override fun equalityComponents(): Iterable<Any> = listOf(this.x, this.y)

    override fun toString(): String = "$x, $y"

    fun distance(another: Location): Int = abs(this.x - another.x) + abs(this.y - another.y)

    fun copy(x: Int = this.x, y: Int = this.y): Either<Error, Location> = of(x, y)

    companion object {
        fun of(x: Int, y: Int): Either<Error, Location> {
            if (x !in MIN_VALUE..MAX_VALUE) return GeneralErrors.valueIsOutOfRange("x", x, 1, 10).left()
            if (y !in MIN_VALUE..MAX_VALUE) return GeneralErrors.valueIsOutOfRange("y", y, 1, 10).left()

            val location = Location(x, y)
            return location.right()
        }

        const val MIN_VALUE = 1
        const val MAX_VALUE = 10
    }
}
