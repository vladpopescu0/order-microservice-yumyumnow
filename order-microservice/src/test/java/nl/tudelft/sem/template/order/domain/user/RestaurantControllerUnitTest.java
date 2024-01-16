package nl.tudelft.sem.template.order.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nl.tudelft.sem.template.order.controllers.RestaurantController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class RestaurantControllerUnitTest {

    @Mock
    transient RestaurantService mockRestaurantService;

    @InjectMocks
    transient RestaurantController restaurantController;

    transient UUID user;
    transient List<UUID> list;
    transient String asian;

    @BeforeEach
    void setup() {
        user = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        list = new ArrayList<>();
        list.add(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        list.add(UUID.fromString("110e8400-e29b-41d4-a716-446655440000"));
        asian = "asian";
    }

    @Test
    void getAllRestaurantsNull() {
        ResponseEntity<List<UUID>> result = restaurantController.getAllRestaurants(null);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getAllRestaurantsValid() {
        when(mockRestaurantService.getAllRestaurants(user)).thenReturn(list);
        ResponseEntity<List<UUID>> result = restaurantController.getAllRestaurants(user);

        verify(mockRestaurantService, times(1)).getAllRestaurants(user);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(list);

    }

    @Test
    void getAllRestaurantsError() {
        when(mockRestaurantService.getAllRestaurants(user)).thenThrow(RuntimeException.class);
        ResponseEntity<List<UUID>> result = restaurantController.getAllRestaurants(user);

        verify(mockRestaurantService, times(1)).getAllRestaurants(user);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void getAllRestaurantsNotFound() {
        when(mockRestaurantService.getAllRestaurants(user)).thenThrow(RuntimeException.class);
        ResponseEntity<List<UUID>> result = restaurantController.getAllRestaurants(user);

        verify(mockRestaurantService, times(1)).getAllRestaurants(user);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }


    @Test
    void getAllRestaurantsWithQueryNullUser() {
        ResponseEntity<List<UUID>> result = restaurantController.getAllRestaurantsWithQuery(null, "query");

        verify(mockRestaurantService, times(0))
                .getAllRestaurantsWithQuery(null, "query");
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getAllRestaurantsWithQueryNullQuery() {
        ResponseEntity<List<UUID>> result = restaurantController.getAllRestaurantsWithQuery(user, null);

        verify(mockRestaurantService, times(0))
                .getAllRestaurantsWithQuery(user, null);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getAllRestaurantsWithQueryValid() {
        when(mockRestaurantService.getAllRestaurantsWithQuery(user, asian)).thenReturn(list);
        ResponseEntity<List<UUID>> result = restaurantController.getAllRestaurantsWithQuery(user, asian);

        verify(mockRestaurantService, times(1))
                .getAllRestaurantsWithQuery(user, asian);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(list);
    }

    @Test
    void getAllRestaurantsWithQueryError() throws UserIDNotFoundException {
        when(mockRestaurantService.getAllRestaurantsWithQuery(user, asian)).thenThrow(RuntimeException.class);

        ResponseEntity<List<UUID>> result = restaurantController.getAllRestaurantsWithQuery(user, asian);

        verify(mockRestaurantService, times(1))
                .getAllRestaurantsWithQuery(user, asian);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}