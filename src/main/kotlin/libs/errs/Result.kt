package libs.errs

import java.util.Objects
import java.util.function.Consumer
import java.util.function.Function

@Deprecated("User Ether instead")
class Result<T, E : Error>(private val value: T?, private val error: E?, private val isSuccess: Boolean) {

    companion object {
        fun <T, E : Error> success(value: T): Result<T, E> {
            Objects.requireNonNull(value)
            return Result(value, null, true)
        }

        fun <E : Error> success(): Result<Void, E> = Result(null, null, true)

        fun <T, E : Error> failure(error: E): Result<T, E> {
            Objects.requireNonNull(error)
            return Result(null, error, false)
        }
    }

    fun isSuccess(): Boolean = isSuccess
    fun isFailure(): Boolean = !isSuccess

    fun getValue(): T {
        check(isSuccess) { "Cannot get value from failure" }
        return value!!
    }

    fun getError(): E {
        check(!isSuccess) { "Cannot get error from success" }
        return error!!
    }

    fun <U> map(mapper: (T) -> U): Result<U, E> = if (isSuccess) success(mapper(value!!)) else failure(error!!)

    fun <U> flatMap(mapper: (T) -> Result<U, E>): Result<U, E> = if (isSuccess) mapper(value!!) else failure(error!!)

    fun onSuccess(handler: Consumer<T>): Result<T, E> {
        if (isSuccess) handler.accept(value!!)
        return this
    }

    fun onFailure(handler: Consumer<E>): Result<T, E> {
        if (isFailure()) handler.accept(error!!)
        return this
    }

    fun <U> fold(onSuccess: Function<T, U>, onFailure: Function<E, U>): U =
        if (isSuccess) onSuccess.apply(value!!) else onFailure.apply(error!!)

    fun <F : Error> mapError(mapper: (E) -> F): Result<T, F> = if (isSuccess) success(value!!) else failure(mapper(error!!))

    fun getValueOrThrow(): T {
        if (isSuccess) return value!!
        throw DomainInvariantException(error!!)
    }

    override fun toString(): String = if (isSuccess) "Success($value)" else "Failure($error)"
}
