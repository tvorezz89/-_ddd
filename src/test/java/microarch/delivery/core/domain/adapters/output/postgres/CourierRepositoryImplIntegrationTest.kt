package microarch.delivery.core.domain.adapters.output.postgres

import microarch.delivery.core.domain.fixture.DataFixtures
import microarch.delivery.core.domain.test.IntegrationTestBase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull

class CourierRepositoryImplIntegrationTest : IntegrationTestBase() {

    @Test
    fun `should save courier`() {
        // arrange
        val courier = DataFixtures.createCourier()
        val order = DataFixtures.createOrder()
        courier.assign(
            orderId = order.getId()!!,
            volume = order.volume,
            location = order.location
        )

        // Нужно, чтобы assignment корректно сохранилось
        orderRepository.save(order)

        // act
        courierRepository.save(courier)

        // assert
        val result = courier.getId()?.let { courierRepository.getById(it) }
        assertNotNull(result)
        assertEquals(courier, result)
    }

    @Test
    fun `when courier saved then get by id should return object`() {
        // arrange
        val courier = DataFixtures.createCourier()
        courierRepository.save(courier)

        // act
        val result = courier.getId()?.let { courierRepository.getById(it) }

        // assert
        assertNotNull(result)
        assertEquals(courier, result)
    }

    @Test
    fun `when courier saved then get by id should return null`() {
        // arrange
        val courier = DataFixtures.createCourier()

        // act
        val result = courier.getId()?.let { courierRepository.getById(it) }

        // assert
        assertNull(result)
    }

    @Test
    fun `when no courier saved should return empty collection`() {
        // act
        val result = courierRepository.getAll()

        // assert
        assertNotNull(result)
        assertEquals(0, result.size)
    }

    @Test
    fun `when few courier saved should return all`() {
        // arrange
        val couriers = listOf(
            DataFixtures.createCourier(name = "courier1"),
            DataFixtures.createCourier(name = "courier2")
        )
        couriers.forEach { courier -> courierRepository.save(courier) }

        // act
        val result = courierRepository.getAll()

        // assert
        assertNotNull(result)
        assertEquals(2, result.size)
        assertTrue(result.containsAll(couriers))
    }
}
