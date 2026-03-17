package microarch.delivery.core.domain.application.query;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import microarch.delivery.core.application.query.GetAllNotCompletedOrdersQuery;
import microarch.delivery.core.application.query.GetAllNotCompletedOrdersQueryHandlerImpl;
import microarch.delivery.core.application.query.dto.GetAllNotCompletedOrdersDto;
import microarch.delivery.core.domain.kernel.Location;
import microarch.delivery.core.domain.model.order.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetAllNotCompletedOrdersQueryHandlerImplTest {

    private GetAllNotCompletedOrdersQueryHandlerImpl handler;
    private final EntityManager entityManager = mock(EntityManager.class);

    @BeforeEach
    void init() {
        handler = new GetAllNotCompletedOrdersQueryHandlerImpl(entityManager);
    }

    @Test
    void handle_shouldWorkProperly() {
        var query = GetAllNotCompletedOrdersQuery.create().getValue();
        var typedQuery = mock(TypedQuery.class);

        var dto = new GetAllNotCompletedOrdersDto(UUID.randomUUID(), Location.create(10, 10).getValue());
        var ordersDto = List.of(dto);

        when(entityManager.createQuery(any(), eq(GetAllNotCompletedOrdersDto.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("statuses"), eq(Set.of(OrderStatus.CREATED, OrderStatus.ASSIGNED))))
                .thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(ordersDto);

        var result = handler.handle(query);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().orders()).isEqualTo(ordersDto);
    }
}
