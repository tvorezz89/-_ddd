package microarch.delivery.adapters.out.postgres;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Общий Postgres-контейнер для всех интеграционных тестов. Контейнер запускается один раз на JVM (ленивая
 * инициализация) и не пересоздается для каждого тестового класса.
 */
public abstract class PostgresIntegrationTestBase {

    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("testdb").withUsername("test").withPassword("test");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}
