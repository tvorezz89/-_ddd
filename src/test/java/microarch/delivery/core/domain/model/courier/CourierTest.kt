package microarch.delivery.core.domain.model.courier

import microarch.delivery.core.domain.model.kernel.Location
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import java.math.BigDecimal
import java.util.UUID

class CourierTest {

    @Test
    fun `when name is empty should not create`() {
        // arrange
        val name = ""
        val location = Location.of(1, 1).getOrNull()!!

        // act
        val result = Courier.of(name, location)

        // assert
        assertTrue(result.isLeft())
        assertEquals("value.is.required", result.leftOrNull()?.code)
    }

    @Test
    fun `should create correctly`() {
        // arrange
        val name = "some name"
        val location = Location.of(1, 1).getOrNull()!!

        // act
        val result = Courier.of(name, location)

        // assert
        assertTrue(result.isRight())
        assertNotNull(result.getOrNull())
        assertEquals(name, result.getOrNull()?.name)
        assertEquals(location, result.getOrNull()?.location)
    }

    @Test
    fun `when current location and target are different should move`() {
        // arrange
        val courier = Courier.of(
            name = "some name",
            location = Location.of(1, 1).getOrNull()!!
        ).getOrNull()!!

        val expected = Location.of(2, 2).getOrNull()!!

        // act
        val result = courier.move(expected)

        // assert
        assertTrue(result.isRight())
        assertEquals(expected, courier.location)
    }

    @Test
    fun `when current location and target are same should not move`() {
        // arrange
        val courier = Courier.of(
            name = "some name",
            location = Location.of(1, 1).getOrNull()!!
        ).getOrNull()!!

        val expected = Location.of(1, 1).getOrNull()!!

        // act
        val result = courier.move(expected)

        // assert
        assertTrue(result.isLeft())
        assertEquals("another.location.required", result.leftOrNull()?.code)
    }

    @Test
    fun `when assigment not let assigned should not complete`() {
        // arrange
        val courier = Courier.of(
            name = "some name",
            location = Location.of(1, 1).getOrNull()!!
        ).getOrNull()!!
        val assigmentId = UUID.randomUUID()

        // act
        val result = courier.completeAssignment(assigmentId)

        // assert
        assertTrue(result.isLeft())
        assertEquals("not.let.assigned", result.leftOrNull()?.code)
    }

    @Test
    fun `when distance between assigment location and courier is more than one cell should not complete`() {
        // arrange
        val courier = Courier.of(
            name = "some name",
            location = Location.of(1, 1).getOrNull()!!
        ).getOrNull()!!

        val assignment = courier.assign(
            orderId = UUID.randomUUID(),
            volume = Volume.of(BigDecimal.ONE).getOrNull()!!,
            location = Location.of(2, 2).getOrNull()!!
        ).getOrNull()!!

        // act
        val result = courier.completeAssignment(assignment.getId()!!)

        // assert
        assertTrue(result.isLeft())
        assertEquals("current.location.too.far.from.target", result.leftOrNull()?.code)
    }

    @Test
    fun `when distance between assigment location and courier is one cell should complete`() {
        // arrange
        val courier = Courier.of(
            name = "some name",
            location = Location.of(1, 1).getOrNull()!!
        ).getOrNull()!!

        val assignment = courier.assign(
            orderId = UUID.randomUUID(),
            volume = Volume.of(BigDecimal.ONE).getOrNull()!!,
            location = Location.of(2, 1).getOrNull()!!
        ).getOrNull()!!

        // act
        val result = courier.completeAssignment(assignment.getId()!!)

        // assert
        assertTrue(result.isRight())
        assertFalse(courier.assignments.contains(assignment))
        assertEquals(AssignmentStatus.COMPLETED, assignment.status)
    }

    @Test
    fun `should assign`() {
        // arrange
        val courier = Courier.of(
            name = "some name",
            location = Location.of(1, 1).getOrNull()!!
        ).getOrNull()!!

        // act
        val result = courier.assign(
            orderId = UUID.randomUUID(),
            volume = Volume.of(BigDecimal.ONE).getOrNull()!!,
            location = Location.of(2, 2).getOrNull()!!
        )

        // assert
        assertTrue(result.isRight())
        assertTrue(courier.assignments.contains(result.getOrNull()!!))
    }

    @Test
    fun `has enough volume when current assignments is empty and volume less than 20 should return true`() {
        // arrange
        val courier = Courier.of(
            name = "some name",
            location = Location.of(1, 1).getOrNull()!!
        ).getOrNull()!!
        val volume = Volume.of(BigDecimal.ONE).getOrNull()!!

        // act
        val result = courier.hasEnoughVolume(volume)

        // assert
        assertTrue(result)
    }

    @Test
    fun `when current assignments is not empty and sum volume is more than 20 should not assign`() {
        // arrange
        val courier = Courier.of(
            name = "some name",
            location = Location.of(1, 1).getOrNull()!!
        ).getOrNull()!!

        courier.assign(
            orderId = UUID.randomUUID(),
            volume = Volume.of(BigDecimal("2.1")).getOrNull()!!,
            location = Location.of(2, 1).getOrNull()!!
        )

        // act
        val result = courier.assign(
            orderId = UUID.randomUUID(),
            volume = Volume.of(BigDecimal("18.0")).getOrNull()!!,
            location = Location.of(2, 1).getOrNull()!!
        )

        // assert
        assertTrue(result.isLeft())
        assertEquals("volume.limit.exceeded", result.leftOrNull()?.code)
    }

    @Test
    fun `has enough volume when current assignments is not empty and sum volume less than 20 should assign`() {
        // arrange
        val courier = Courier.of(
            name = "some name",
            location = Location.of(1, 1).getOrNull()!!
        ).getOrNull()!!

        courier.assign(
            orderId = UUID.randomUUID(),
            volume = Volume.of(BigDecimal.ONE).getOrNull()!!,
            location = Location.of(2, 1).getOrNull()!!
        )

        val volume = Volume.of(BigDecimal("18.5")).getOrNull()!!

        // act
        val result = courier.hasEnoughVolume(volume)

        // assert
        assertTrue(result)
    }

    @Test
    fun `has enough volume when current assignments is not empty and sum volume is equals 20 should assign`() {
        // arrange
        val courier = Courier.of(
            name = "some name",
            location = Location.of(1, 1).getOrNull()!!
        ).getOrNull()!!

        courier.assign(
            orderId = UUID.randomUUID(),
            volume = Volume.of(BigDecimal.TWO).getOrNull()!!,
            location = Location.of(2, 1).getOrNull()!!
        )

        val volume = Volume.of(BigDecimal("18.0")).getOrNull()!!

        // act
        val result = courier.hasEnoughVolume(volume)

        // assert
        assertTrue(result)
    }

    @Test
    fun `has enough volume when current assignments is not empty and sum volume is more than 20 should return false`() {
        // arrange
        val courier = Courier.of(
            name = "some name",
            location = Location.of(1, 1).getOrNull()!!
        ).getOrNull()!!

        courier.assign(
            orderId = UUID.randomUUID(),
            volume = Volume.of(BigDecimal("2.1")).getOrNull()!!,
            location = Location.of(2, 1).getOrNull()!!
        )

        val volume = Volume.of(BigDecimal("18.0")).getOrNull()!!

        // act
        val result = courier.hasEnoughVolume(volume)

        // assert
        assertFalse(result)
    }

    @Test
    fun `when assignment has already been assigned should not assign`() {
        // arrange
        val courier = Courier.of(
            name = "some name",
            location = Location.of(1, 1).getOrNull()!!
        ).getOrNull()!!

        val orderId = UUID.randomUUID()
        val assigment = courier.assign(
            orderId = orderId,
            volume = Volume.of(BigDecimal("2.1")).getOrNull()!!,
            location = Location.of(2, 1).getOrNull()!!
        ).getOrNull()!!

        // act
        val result = courier.assign(
            orderId = orderId,
            volume = Volume.of(BigDecimal("2.1")).getOrNull()!!,
            location = Location.of(2, 1).getOrNull()!!
        )

        // assert
        assertTrue(result.isLeft())
        assertEquals("already.assigned", result.leftOrNull()?.code)
    }
}
