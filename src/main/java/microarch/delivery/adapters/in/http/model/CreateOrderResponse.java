package microarch.delivery.adapters.in.http.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.*;
import javax.annotation.Generated;

/**
 * CreateOrderResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-01T23:50:55.927787+03:00[Europe/Moscow]")
public class CreateOrderResponse {

    private UUID orderId;

    public CreateOrderResponse orderId(UUID orderId) {
        this.orderId = orderId;
        return this;
    }

    /**
     * Get orderId
     *
     * @return orderId
     */
    @Valid
    @Schema(name = "orderId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("orderId")
    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateOrderResponse createOrderResponse = (CreateOrderResponse) o;
        return Objects.equals(this.orderId, createOrderResponse.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class CreateOrderResponse {\n");
        sb.append("    orderId: ").append(toIndentedString(orderId)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
