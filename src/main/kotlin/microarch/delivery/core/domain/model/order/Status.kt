package microarch.delivery.core.domain.model.order

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class Status {
    CREATED,
    ASSIGNED,
    COMPLETED;

    @JsonValue
    fun toValue(): String = name.lowercase()

    companion object {
        @JsonCreator
        fun fromValue(value: String): Status = Status.valueOf(value.uppercase())
    }
}
