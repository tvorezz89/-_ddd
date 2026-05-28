package libs.errs

import java.util.Objects
import java.util.function.Consumer
import java.util.function.Function

@Deprecated("User Ether instead")
class UnitResult<E : Error>(private val isSuccess: Boolean, private val error: E?) {

    companion object {
        fun <E : Error> success(): UnitResult<E> = UnitResult(true, null)

        fun <E : Error> failure(error: E): UnitResult<E> {
            Objects.requireNonNull(error, "Error must not be null on failure")
            return UnitResult(false, error)
        }

        fun <E : Error> from(result: Result<Void, E>): UnitResult<E> = if (result.isSuccess()) success() else failure(result.getError())
    }

    fun isSuccess(): Boolean = isSuccess
    fun isFailure(): Boolean = !isSuccess

    fun getError(): E {
        check(!isSuccess) { "Cannot get error from success" }
        return error!!
    }

    fun onSuccess(handler: Runnable): UnitResult<E> {
        if (isSuccess) handler.run()
        return this
    }

    fun onFailure(handler: Consumer<E>): UnitResult<E> {
        if (isFailure()) handler.accept(error!!)
        return this
    }

    fun <U> fold(onSuccess: Function<Void?, U>, onFailure: (E) -> U): U = if (isSuccess) onSuccess.apply(null) else onFailure(error!!)

    fun merge(other: UnitResult<E>): UnitResult<E> {
        if (this.isFailure()) return this
        if (other.isFailure()) return other
        return success()
    }

    fun toResult(): Result<Void, E> = if (isFailure()) Result.failure(error!!) else Result.success()

    fun getOrElseThrow(exceptionMapper: (E) -> RuntimeException) {
        if (isSuccess) return
        throw exceptionMapper(error!!)
    }

    fun getOrElseThrow() {
        if (isSuccess) return
        throw DomainInvariantException(error!!)
    }

    override fun toString(): String = if (isSuccess) "Success" else "Failure($error)"
}
