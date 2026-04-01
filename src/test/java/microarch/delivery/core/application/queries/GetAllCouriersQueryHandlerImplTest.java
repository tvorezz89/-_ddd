package microarch.delivery.core.application.queries;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetAllCouriersQueryHandlerImplTest {

    private final CourierRepository courierRepository = mock(CourierRepository.class);
    private final GetAllCouriersQueryHandlerImpl handler = new GetAllCouriersQueryHandlerImpl(courierRepository);

    @Test
    void handle_shouldReturnAllCouriers() {
        var courier1 = Courier.create("Ivan", Location.create(1, 2).getValue()).getValue();
        var courier2 = Courier.create("Petr", Location.create(5, 5).getValue()).getValue();
        when(courierRepository.findAll()).thenReturn(List.of(courier1, courier2));

        var result = handler.handle(new GetAllCouriersQuery());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Ivan");
        assertThat(result.get(0).getLocationX()).isEqualTo(1);
        assertThat(result.get(0).getLocationY()).isEqualTo(2);
        assertThat(result.get(1).getName()).isEqualTo("Petr");
    }

    @Test
    void handle_shouldReturnEmptyList_whenNoCouriers() {
        when(courierRepository.findAll()).thenReturn(List.of());

        var result = handler.handle(new GetAllCouriersQuery());

        assertThat(result).isEmpty();
    }
}
