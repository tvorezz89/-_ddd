package microarch.delivery

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@EnableJpaRepositories(basePackages = ["microarch.delivery.adapters.output.postgres"])
@EntityScan(basePackages = ["microarch.delivery.core.domain.model", "microarch.delivery.adapters.output.postgres.outbox"])
@SpringBootApplication
class DeliveryApplication

fun main(args: Array<String>) {
    org.springframework.boot.SpringApplication.run(DeliveryApplication::class.java, *args)
}
