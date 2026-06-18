package microarch.delivery.core.domain.model.courier

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import libs.ddd.ValueObject
import libs.errs.Error
import libs.errs.GeneralErrors
import java.math.BigDecimal
import java.math.RoundingMode

@Embeddable
class Volume private constructor(@Column("volume") val litres: BigDecimal) : ValueObject<Volume>() {

    override fun equalityComponents(): Iterable<Any> = listOf(this.litres)

    operator fun plus(other: Volume): Volume = Volume(litres + other.litres)

    override fun compareTo(other: Volume): Int = litres.compareTo(other.litres)

    companion object {
        fun of(litres: BigDecimal): Either<Error, Volume> {
            if (litres.compareTo(BigDecimal.ZERO) != 1) {
                return GeneralErrors.valueMustBeGreaterThan("litres", litres, BigDecimal.ZERO).left()
            }

            val volume = Volume(litres.setScale(SCALE, RoundingMode.HALF_UP))
            return volume.right()
        }

        private const val SCALE = 1
    }
}
