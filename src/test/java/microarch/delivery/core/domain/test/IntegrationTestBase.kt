package microarch.delivery.core.domain.test

import microarch.delivery.DeliveryApplication
import microarch.delivery.adapters.output.postgres.CourierJpaRepository
import microarch.delivery.adapters.output.postgres.OrderJpaRepository
import microarch.delivery.core.ports.CourierRepository
import microarch.delivery.core.ports.OrderRepository
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

@Import(TestTemplateConfiguration::class)
@SpringBootTest(classes = [DeliveryApplication::class])
abstract class IntegrationTestBase {

    @Autowired
    protected lateinit var courierRepository: CourierRepository

    @Autowired
    protected lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var courierJpaRepository: CourierJpaRepository

    @Autowired
    private lateinit var orderJpaRepository: OrderJpaRepository

    @AfterEach
    fun clearPostgres() {
        courierJpaRepository.deleteAll()
        orderJpaRepository.deleteAll()
    }
}

@TestConfiguration(proxyBeanMethods = false)
class TestTemplateConfiguration {

    @Bean
    @ServiceConnection
    fun definePostgres(): PostgreSQLContainer<*> = PostgreSQLContainer(
        DockerImageName.parse("postgres:16.6-alpine")
            .asCompatibleSubstituteFor("postgres")
    )
        .withDatabaseName("ifrs_rw_non_retail")
        .withUsername("postgres")
        .withPassword("postgres")
        .waitingFor(Wait.forListeningPort())
        .withReuse(true)
}
