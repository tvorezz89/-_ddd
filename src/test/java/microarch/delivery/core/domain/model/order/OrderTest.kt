package microarch.delivery.core.domain.model.order

import microarch.delivery.core.domain.model.courier.Volume
import microarch.delivery.core.domain.model.kernel.Location
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import java.math.BigDecimal
import java.util.UUID

class OrderTest {

    @Test
    fun `should create correctly`() {
        // arrange
        val id = UUID.randomUUID()
        val volume = Volume.of(BigDecimal("0.1")).getOrNull()!!
        val location = Location.of(1, 1).getOrNull()!!

        // act
        val result = Order.of(id, volume, location)

        // assert
        assertTrue(result.isRight())
        assertNotNull(result.getOrNull())
        assertEquals(id, result.getOrNull()?.getId())
        assertEquals(volume, result.getOrNull()?.volume)
        assertEquals(location, result.getOrNull()?.location)
        assertEquals(Status.CREATED, result.getOrNull()?.status)
    }

    @Test
    fun `when current status CREATED should assign`() {
        // arrange
        val order = Order.of(
            id = UUID.randomUUID(),
            volume = Volume.of(BigDecimal("0.1")).getOrNull()!!,
            location = Location.of(1, 1).getOrNull()!!
        ).getOrNull()!!

        // act
        val result = order.assign()

        // assert
        assertTrue(result.isRight())
        assertNotNull(result.getOrNull())
        assertEquals(Status.ASSIGNED, order.status)
    }

    @Test
    fun `when current status is ASSIGNED should not assign`() {
        // arrange
        val order = Order.of(
            id = UUID.randomUUID(),
            volume = Volume.of(BigDecimal("0.1")).getOrNull()!!,
            location = Location.of(1, 1).getOrNull()!!
        ).getOrNull()!!

        order.assign()

        // act
        val result = order.assign()

        // assert
        assertTrue(result.isLeft())
        assertEquals("can.assign.only.created.order", result.leftOrNull()?.code)
    }

    @Test
    fun `when current status is COMPLETED should not assign`() {
        // arrange
        val order = Order.of(
            id = UUID.randomUUID(),
            volume = Volume.of(BigDecimal("0.1")).getOrNull()!!,
            location = Location.of(1, 1).getOrNull()!!
        ).getOrNull()!!

        order.assign()
        order.complete()

        // act
        val result = order.assign()

        // assert
        assertTrue(result.isLeft())
        assertEquals("can.assign.only.created.order", result.leftOrNull()?.code)
    }

    @Test
    fun `when current status is ASSIGNED should complete`() {
        // arrange
        val order = Order.of(
            id = UUID.randomUUID(),
            volume = Volume.of(BigDecimal("0.1")).getOrNull()!!,
            location = Location.of(1, 1).getOrNull()!!
        ).getOrNull()!!
        order.assign()

        // act
        val result = order.complete()

        // assert
        assertTrue(result.isRight())
        assertNotNull(result.getOrNull())
        assertEquals(Status.COMPLETED, order.status)
    }

    @Test
    fun `when current status is CREATED should not complete`() {
        // arrange
        val order = Order.of(
            id = UUID.randomUUID(),
            volume = Volume.of(BigDecimal("0.1")).getOrNull()!!,
            location = Location.of(1, 1).getOrNull()!!
        ).getOrNull()!!

        // act
        val result = order.complete()

        // assert
        assertTrue(result.isLeft())
        assertEquals("can.complete.only.assigned.order", result.leftOrNull()?.code)
    }

    @Test
    fun `when current status is COMPLETED should not complete`() {
        // arrange
        val order = Order.of(
            id = UUID.randomUUID(),
            volume = Volume.of(BigDecimal("0.1")).getOrNull()!!,
            location = Location.of(1, 1).getOrNull()!!
        ).getOrNull()!!

        order.assign()
        order.complete()

        // act
        val result = order.complete()

        // assert
        assertTrue(result.isLeft())
        assertEquals("can.complete.only.assigned.order", result.leftOrNull()?.code)
    }
}
