package libs.errs

import java.util.UUID

object Guard {

    private val EMPTY_UUID = UUID(0L, 0L)

    @SafeVarargs
    fun combine(vararg errors: Error?): Error? = errors.firstOrNull { it != null }

    fun againstNullOrEmpty(value: String?, paramName: String): Error? {
        if (value.isNullOrBlank()) return GeneralErrors.valueIsRequired(paramName)
        return null
    }

    fun againstNullOrEmpty(collection: Collection<*>?, paramName: String): Error? {
        if (collection.isNullOrEmpty()) return GeneralErrors.valueIsRequired(paramName)
        return null
    }

    fun againstNullOrEmpty(uuid: UUID?, paramName: String): Error? {
        if (uuid == null || uuid == EMPTY_UUID) return GeneralErrors.valueIsRequired(paramName)
        return null
    }

    fun <T : Comparable<T>> againstGreaterThan(value: T?, max: T, paramName: String): Error? {
        if (value == null || value > max) return GeneralErrors.valueMustBeLessThan(paramName, value!!, max)
        return null
    }

    fun <T : Comparable<T>> againstGreaterOrEqual(value: T?, max: T, paramName: String): Error? {
        if (value == null || value >= max) return GeneralErrors.valueMustBeLessOrEqual(paramName, value!!, max)
        return null
    }

    fun <T : Comparable<T>> againstLessThan(value: T?, min: T, paramName: String): Error? {
        if (value == null || value < min) return GeneralErrors.valueMustBeLessThan(paramName, value!!, min)
        return null
    }

    fun <T : Comparable<T>> againstLessOrEqual(value: T?, min: T, paramName: String): Error? {
        if (value == null || value <= min) return GeneralErrors.valueMustBeGreaterOrEqual(paramName, value!!, min)
        return null
    }

    fun <T : Comparable<T>> againstOutOfRange(value: T?, min: T, max: T, paramName: String): Error? {
        if (value == null || value < min || value > max) {
            return GeneralErrors.valueIsOutOfRange(paramName, value!!, min, max)
        }
        return null
    }
}
