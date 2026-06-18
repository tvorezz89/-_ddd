package microarch.delivery.core.domain.model.courier

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal

class VolumeTest {

    @Test
    fun `when value of different object are equals then objects should be equals`() {
        // arrange
        val first = Volume.of(BigDecimal("2.5"))
        val second = Volume.of(BigDecimal("2.5"))

        // act && assert
        assertEquals(first, second)
    }

    @Test
    fun `when value of different object are not equals then objects should not be equals`() {
        // arrange
        val first = Volume.of(BigDecimal("2.5"))
        val second = Volume.of(BigDecimal("3"))

        // act && assert
        assertNotEquals(first, second)
    }

    @ParameterizedTest
    @MethodSource("invalidValues")
    fun `when value is less or equals zero should not create`(value: BigDecimal) {
        // arrange
        val result = Volume.of(value)

        // act && assert
        assertTrue(result.isLeft())
        assertEquals("value.must.be.greater.than", result.leftOrNull()?.code)
    }

    @Test
    fun `when value is greater zero should create`() {
        // arrange
        val expected = BigDecimal("0.1")

        // act
        val result = Volume.of(expected)

        // assert
        assertTrue(result.isRight())
        assertNotNull(result.getOrNull())
        assertEquals(expected, result.getOrNull()?.litres)
    }

    @Test
    fun `when create should round`() {
        // arrange
        val expected = BigDecimal("0.2")

        // act
        val result = Volume.of(BigDecimal("0.15"))

        // assert
        assertTrue(result.isRight())
        assertNotNull(result.getOrNull())
        assertEquals(expected, result.getOrNull()?.litres)
    }

    @Test
    fun `should plus correctly`() {
        // arrange
        val first = Volume.of(BigDecimal("2.5")).getOrNull()!!
        val second = Volume.of(BigDecimal("0.6")).getOrNull()!!
        val expected = Volume.of(BigDecimal("3.1")).getOrNull()!!

        // act
        val result = first + second

        // assert
        assertEquals(expected, result)
    }

    @Test
    fun `when first volume less than second should compare correctly`() {
        // arrange
        val first = Volume.of(BigDecimal("0.2")).getOrNull()!!
        val second = Volume.of(BigDecimal("0.3")).getOrNull()!!

        // act
        val result = first < second

        // assert
        assertTrue(result)
    }

    @Test
    fun `when first volume less or equals than second should compare correctly`() {
        // arrange
        val first = Volume.of(BigDecimal("0.2")).getOrNull()!!
        val second = Volume.of(BigDecimal("0.2")).getOrNull()!!

        // act
        val result = first <= second

        // assert
        assertTrue(result)
    }

    @Test
    fun `when first volume more than second should compare correctly`() {
        // arrange
        val first = Volume.of(BigDecimal("0.3")).getOrNull()!!
        val second = Volume.of(BigDecimal("0.2")).getOrNull()!!

        // act
        val result = first > second

        // assert
        assertTrue(result)
    }

    @Test
    fun `when first volume more or equals than second should compare correctly`() {
        // arrange
        val first = Volume.of(BigDecimal("0.2")).getOrNull()!!
        val second = Volume.of(BigDecimal("0.2")).getOrNull()!!

        // act
        val result = first >= second

        // assert
        assertTrue(result)
    }

    companion object {
        @JvmStatic
        fun invalidValues(): List<Arguments> = listOf(
            Arguments.of(BigDecimal.ZERO),
            Arguments.of(BigDecimal("-1"))
        )
    }
}
