package microarch.delivery.core.domain.adapters.output.postgres

import microarch.delivery.core.domain.fixture.DataFixtures
import microarch.delivery.core.domain.test.IntegrationTestBase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import java.util.UUID

class OrderRepositoryImplIntegrationTest : IntegrationTestBase() {

    @Test
    fun `should save order`() {
        // arrange
        val order = DataFixtures.createOrder()

        // act
        orderRepository.save(order)

        // assert
        val result = order.getId()?.let { orderRepository.getById(it) }
        assertNotNull(result)
        assertEquals(order, result)
    }

    @Test
    fun `when order saved then get by id should return object`() {
        // arrange
        val orderId = UUID.randomUUID()
        val order = DataFixtures.createOrder(orderId)

        orderRepository.save(order)

        // act
        val result = orderRepository.getById(orderId)

        // assert
        assertNotNull(result)
        assertEquals(order, result)
    }

    @Test
    fun `when no order saved then get by id should return null`() {
        // arrange
        val orderId = UUID.randomUUID()

        // act
        val result = orderRepository.getById(orderId)

        // assert
        assertNull(result)
    }

    @Test
    fun `when there is no order with status CREATED then get any created should return null`() {
        // arrange
        val assignedOrder = DataFixtures.createOrder()
        assignedOrder.assign()
        orderRepository.save(assignedOrder)

        val completedOrder = DataFixtures.createOrder()
        completedOrder.assign()
        completedOrder.complete()
        orderRepository.save(completedOrder)

        // act
        val result = orderRepository.getAnyCreated()

        // assert
        assertNull(result)
    }

    @Test
    fun `when there is order with status CREATED then get any created should return null`() {
        // arrange
        val assignedOrder = DataFixtures.createOrder()
        assignedOrder.assign()
        orderRepository.save(assignedOrder)

        val completedOrder = DataFixtures.createOrder()
        completedOrder.assign()
        completedOrder.complete()
        orderRepository.save(completedOrder)

        val createdOrder = DataFixtures.createOrder()
        orderRepository.save(createdOrder)

        // act
        val result = orderRepository.getAnyCreated()

        // assert
        assertNotNull(result)
        assertEquals(createdOrder, result)
    }

    @Test
    fun `when there is no order with status ASSIGNED then get all assigned should return empty collection`() {
        // arrange
        val createdOrder = DataFixtures.createOrder()
        orderRepository.save(createdOrder)

        val completedOrder = DataFixtures.createOrder()
        completedOrder.assign()
        completedOrder.complete()
        orderRepository.save(completedOrder)

        // act
        val result = orderRepository.getAllAssigned()

        // assert
        assertNotNull(result)
        assertEquals(0, result.size)
    }

    @Test
    fun `when there are few orders with status ASSIGNED then get all assigned should return all`() {
        // arrange
        val createdOrder = DataFixtures.createOrder()
        orderRepository.save(createdOrder)

        val completedOrder = DataFixtures.createOrder()
        completedOrder.assign()
        completedOrder.complete()
        orderRepository.save(completedOrder)

        val assignedOrder1 = DataFixtures.createOrder()
        assignedOrder1.assign()
        orderRepository.save(assignedOrder1)

        val assignedOrder2 = DataFixtures.createOrder()
        assignedOrder2.assign()
        orderRepository.save(assignedOrder2)

        // act
        val result = orderRepository.getAllAssigned()

        // assert
        assertNotNull(result)
        assertEquals(2, result.size)
        assertEquals(result.sorted(), listOf(assignedOrder1, assignedOrder2).sorted())
    }
}
