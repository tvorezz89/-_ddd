package microarch.delivery.core.domain.services

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import clients.geo.location
import libs.errs.Error
import libs.errs.GeneralErrors
import microarch.delivery.core.domain.model.courier.Courier
import microarch.delivery.core.domain.model.order.Order
import microarch.delivery.core.domain.model.order.Status
import org.springframework.stereotype.Service

interface DispatchService {
    fun dispatch(order: Order, couriers: List<Courier>): Either<Error, Courier>
}

@Service
class DispatchServiceImpl : DispatchService {

    override fun dispatch(order: Order, couriers: List<Courier>): Either<Error, Courier> = either {
        ensure(order.status == Status.CREATED) {
            GeneralErrors.valueIsInvalid("order.status", order.status)
        }
        ensure(order.getId() != null) {
            GeneralErrors.valueIsRequired("order.id")
        }

        val courier = couriers.asSequence()
            .filter { it.hasEnoughVolume(order.volume) }
            .minByOrNull { it.location.distance(order.location) }
            ?: raise(noSuitableCourier())

        courier.assign(orderId = order.getId()!!, volume = order.volume, location = order.location).bind()
        order.assign()

        courier
    }

    companion object {
        private fun noSuitableCourier(): Error = Error.of(
            "no.suitable.courier",
            "There is no suitable courier for this order."
        )
    }
}
