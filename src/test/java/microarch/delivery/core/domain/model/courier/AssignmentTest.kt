package microarch.delivery.core.domain.model.courier

import microarch.delivery.core.domain.model.kernel.Location
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import java.math.BigDecimal
import java.util.UUID

class AssignmentTest {

    @Test
    fun `when status is COMPLETED should not create`() {
        // arrange
        val orderId = UUID.randomUUID()
        val volume = Volume.of(BigDecimal.ONE).getOrNull()!!
        val location = Location.of(5, 5).getOrNull()!!
        val status = Status.COMPLETED

        // act
        val result = Assignment.of(orderId, volume, location, status)

        // assert
        assertTrue(result.isLeft())
        assertEquals("value.is.invalid", result.leftOrNull()?.code)
    }

    @Test
    fun `when status is ASSIGNED should create`() {
        // arrange
        val orderId = UUID.randomUUID()
        val volume = Volume.of(BigDecimal.ONE).getOrNull()!!
        val location = Location.of(5, 5).getOrNull()!!
        val status = Status.ASSIGNED

        // act
        val result = Assignment.of(orderId, volume, location, status)

        // assert
        assertTrue(result.isRight())

        val assignment = result.getOrNull()
        assertNotNull(result)
        assertEquals(orderId, assignment?.orderId)
        assertEquals(volume, assignment?.volume)
        assertEquals(location, assignment?.location)
        assertEquals(status, assignment?.status)
    }

    @Test
    fun `when distance between current location and target is one cell should complete`() {
        // arrange
        val assignment = Assignment.of(
            orderId = UUID.randomUUID(),
            volume = Volume.of(BigDecimal.ONE).getOrNull()!!,
            location = Location.of(5, 5).getOrNull()!!,
            status = Status.ASSIGNED
        ).getOrNull()!!
        val currentLocation = Location.of(5, 4).getOrNull()!!

        // act
        val result = assignment.complete(currentLocation)

        // assert
        assertTrue(result.isRight())
        assertNotNull(result.getOrNull())
        assertEquals(Status.COMPLETED, assignment.status)
    }

    @Test
    fun `when current location and target are the same cell should complete`() {
        // arrange
        val assignment = Assignment.of(
            orderId = UUID.randomUUID(),
            volume = Volume.of(BigDecimal.ONE).getOrNull()!!,
            location = Location.of(5, 5).getOrNull()!!,
            status = Status.ASSIGNED
        ).getOrNull()!!
        val currentLocation = Location.of(5, 5).getOrNull()!!

        // act
        val result = assignment.complete(currentLocation)

        // assert
        assertTrue(result.isRight())
        assertNotNull(result.getOrNull())
        assertEquals(Status.COMPLETED, assignment.status)
    }

    @Test
    fun `when distance between current location and target more than one cell should not complete`() {
        // arrange
        val assignment = Assignment.of(
            orderId = UUID.randomUUID(),
            volume = Volume.of(BigDecimal.ONE).getOrNull()!!,
            location = Location.of(5, 5).getOrNull()!!,
            status = Status.ASSIGNED
        ).getOrNull()!!
        val currentLocation = Location.of(4, 4).getOrNull()!!

        // act
        val result = assignment.complete(currentLocation)

        // assert
        assertTrue(result.isLeft())
        assertEquals("current.location.too.far.from.target", result.leftOrNull()?.code)
    }
}
