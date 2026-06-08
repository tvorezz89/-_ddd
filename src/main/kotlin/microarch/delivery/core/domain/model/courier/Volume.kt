package microarch.delivery.core.domain.model.courier

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import libs.ddd.ValueObject
import libs.errs.Error
import libs.errs.GeneralErrors
import java.math.BigDecimal
import java.math.RoundingMode

class Volume private constructor(val value: BigDecimal) : ValueObject<Volume>() {

    override fun equalityComponents(): Iterable<Any> = listOf(this.value)

    operator fun plus(other: Volume): Volume = Volume(value + other.value)

    override fun compareTo(other: Volume): Int = value.compareTo(other.value)

    companion object {
        fun of(value: BigDecimal): Either<Error, Volume> {
            if (value.compareTo(BigDecimal.ZERO) != 1) {
                return GeneralErrors.valueMustBeGreaterThan("value", value, BigDecimal.ZERO).left()
            }

            val volume = Volume(value.setScale(SCALE, RoundingMode.HALF_UP))
            return volume.right()
        }

        private const val SCALE = 1
    }
}
