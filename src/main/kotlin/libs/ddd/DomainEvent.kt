package libs.ddd

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.context.ApplicationEvent
import java.time.Instant
import java.util.UUID

abstract class DomainEvent(source: Any) : ApplicationEvent(source) {

    val eventId: UUID = UUID.randomUUID()
    val occurredOnUtc: Instant = Instant.now()

    protected constructor() : this("default")

    @JsonIgnore
    override fun getSource(): Any = super.getSource()
}
