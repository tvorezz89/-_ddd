package microarch.delivery.core.domain.model.courier

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import libs.ddd.ValueObject
import libs.errs.Error
import libs.errs.GeneralErrors
import java.math.BigDecimal

class Volume private constructor(val value: BigDecimal) : ValueObject<Volume>() {

    override fun equalityComponents(): Iterable<Any> = listOf(this.value)

    companion object {
        fun of(value: BigDecimal): Either<Error, Volume> {
            if (value.compareTo(BigDecimal.ZERO) != 1) {
                return GeneralErrors.valueMustBeGreaterThan("value", value, BigDecimal.ZERO).left()
            }

            val volume = Volume(value)
            return volume.right()
        }
    }
}
