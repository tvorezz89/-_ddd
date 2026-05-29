package microarch.delivery.core.domain.model.kernel

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class LocationTest {

    @ParameterizedTest(name = "when x is {0} and y is {1} should not create")
    @MethodSource("invalidCoordinates")
    fun `when coordinates are invalid should not create`(x: Int, y: Int) {
        // arrange & act
        val result = Location.of(x, y)

        // assert
        assertTrue(result.isLeft())
        assertEquals(result.leftOrNull()?.code, "value.is.out.of.range")
    }

    @Test
    fun `when x and y of different object are equals then objects should be equals`() {
        // arrange
        val first = Location.of(x = 5, y = 3).getOrNull()!!
        val second = Location.of(x = 5, y = 3).getOrNull()!!

        // act
        val result = first.equals(second)

        // assert
        assertTrue(result)
    }

    @Test
    fun `when x or y of different object are not equals then objects should not be equals`() {
        // arrange
        val first = Location.of(x = 5, y = 3).getOrNull()!!
        val second = Location.of(x = 5, y = 2).getOrNull()!!

        // act
        val result = first.equals(second)

        // assert
        assertFalse(result)
    }

    @Test
    fun `when objects has the same x and y then distance should be zero`() {
        // arrange
        val first = Location.of(x = 5, y = 3).getOrNull()!!
        val second = Location.of(x = 5, y = 3).getOrNull()!!

        // act
        val result = first.distance(second)

        // assert
        assertEquals(0, result)
    }

    @Test
    fun `when objects has different x and y then distance should be calculated`() {
        // arrange
        val first = Location.of(x = 2, y = 6).getOrNull()!!
        val second = Location.of(x = 4, y = 9).getOrNull()!!

        // act
        val result = first.distance(second)

        // assert
        assertEquals(5, result)
    }

    companion object {
        @JvmStatic
        fun invalidCoordinates(): List<Arguments> = listOf(
            Arguments.of(0, 1),
            Arguments.of(11, 1),
            Arguments.of(1, 0),
            Arguments.of(1, 11)
        )
    }
}
