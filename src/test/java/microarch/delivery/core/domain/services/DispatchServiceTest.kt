package microarch.delivery.core.domain.services

import microarch.delivery.core.domain.model.courier.Courier
import microarch.delivery.core.domain.model.courier.Volume
import microarch.delivery.core.domain.model.kernel.Location
import microarch.delivery.core.domain.model.order.Order
import microarch.delivery.core.domain.model.order.Status
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class DispatchServiceTest {

    val sut = DispatchServiceImpl()

    @Test
    fun `when order status is ASSIGNED should not dispatch`() {
        // arrange
        val order = Order.of(
            id = UUID.randomUUID(),
            volume = Volume.of(BigDecimal.ONE).getOrNull()!!,
            location = Location.of(x = 1, y = 1).getOrNull()!!
        ).getOrNull()!!
        order.assign()

        val couriers: List<Courier> = emptyList()

        // act
        val result = sut.dispatch(order, couriers)

        // assert
        assertTrue(result.isLeft())
        assertEquals("value.is.invalid", result.leftOrNull()?.code)
    }

    @Test
    fun `when order status is COMPLETED should not dispatch`() {
        // arrange
        val order = Order.of(
            id = UUID.randomUUID(),
            volume = Volume.of(BigDecimal.ONE).getOrNull()!!,
            location = Location.of(x = 1, y = 1).getOrNull()!!
        ).getOrNull()!!
        order.assign()
        order.complete()

        val couriers: List<Courier> = emptyList()

        // act
        val result = sut.dispatch(order, couriers)

        // assert
        assertTrue(result.isLeft())
        assertEquals("value.is.invalid", result.leftOrNull()?.code)
    }

    @Test
    fun `when courier has no enough volume should not dispatch`() {
        // arrange
        val order = Order.of(
            id = UUID.randomUUID(),
            volume = Volume.of(BigDecimal.ONE).getOrNull()!!,
            location = Location.of(x = 1, y = 1).getOrNull()!!
        ).getOrNull()!!

        val courier = Courier.of(
            name = "courier",
            location = Location.of(x = 10, y = 10).getOrNull()!!
        ).getOrNull()!!

        courier.assign(
            orderId = order.getId()!!,
            location = Location.of(x = 10, y = 10).getOrNull()!!,
            volume = Volume.of(BigDecimal("20.0")).getOrNull()!!
        )

        // act
        val result = sut.dispatch(order, listOf(courier))

        // assert
        assertTrue(result.isLeft())
        assertEquals("no.suitable.courier", result.leftOrNull()?.code)
    }

    @Test
    fun `when few couriers has enough volume should take with nearest and create assigment`() {
        // arrange
        val order = Order.of(
            id = UUID.randomUUID(),
            volume = Volume.of(BigDecimal.ONE).getOrNull()!!,
            location = Location.of(x = 1, y = 1).getOrNull()!!
        ).getOrNull()!!

        val courier1 = Courier.of(
            name = "courier",
            location = Location.of(x = 10, y = 10).getOrNull()!!
        ).getOrNull()!!
        val courier2 = Courier.of(
            name = "courier",
            location = Location.of(x = 5, y = 5).getOrNull()!!
        ).getOrNull()!!

        // act
        val result = sut.dispatch(order, listOf(courier1, courier2))

        // assert
        assertTrue(result.isRight())
        assertEquals(courier2, result.getOrNull()!!)

        val assigment = result.getOrNull()!!.assignments
        assertEquals(1, assigment.size)
        assertEquals(order.getId()!!, assigment[0].orderId)
        assertEquals(order.volume, assigment[0].volume)
        assertEquals(order.location, assigment[0].location)
        assertEquals(Status.ASSIGNED, order.status)
    }
}
