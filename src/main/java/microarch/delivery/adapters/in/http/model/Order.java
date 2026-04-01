package microarch.delivery.adapters.in.http.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.UUID;
import microarch.delivery.adapters.in.http.model.Location;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.*;
import javax.annotation.Generated;

/**
 * Order
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-04-01T23:50:55.927787+03:00[Europe/Moscow]")
public class Order {

    private UUID id;

    private Location location;

    public Order() {
        super();
    }

    /**
     * Constructor with only required parameters
     */
    public Order(UUID id, Location location) {
        this.id = id;
        this.location = location;
    }

    public Order id(UUID id) {
        this.id = id;
        return this;
    }

    /**
     * Идентификатор
     *
     * @return id
     */
    @NotNull
    @Valid
    @Schema(name = "id", description = "Идентификатор", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("id")
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Order location(Location location) {
        this.location = location;
        return this;
    }

    /**
     * Get location
     *
     * @return location
     */
    @NotNull
    @Valid
    @Schema(name = "location", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("location")
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return Objects.equals(this.id, order.id) && Objects.equals(this.location, order.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, location);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Order {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    location: ").append(toIndentedString(location)).append("\n");
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
