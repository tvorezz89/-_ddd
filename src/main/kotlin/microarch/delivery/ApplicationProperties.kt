package microarch.delivery

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app")
class ApplicationProperties {

    val grpc = Grpc()
    val kafka = Kafka()
}

class Grpc {
    val geoService = GeoService()
}

class GeoService {
    var host: String? = null
    var port: Int = 0
}

class Kafka {
    var stocksEventsTopic: String? = null
    var basketsEventsTopic: String? = null
}
