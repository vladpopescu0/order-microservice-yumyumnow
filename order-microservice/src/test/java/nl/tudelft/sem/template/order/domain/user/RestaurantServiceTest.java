package nl.tudelft.sem.template.order.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import nl.tudelft.sem.template.model.Address;
import nl.tudelft.sem.template.user.services.MockLocationService;
import nl.tudelft.sem.template.user.services.UserMicroServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {
    @Mock
    transient UserMicroServiceService mockUserService;
    @Mock
    transient MockLocationService mockLocationService;
    @InjectMocks
    transient RestaurantService restaurantService;

    transient String location;
    transient Address address;
    transient UUID user;
    transient UUID user11;

    transient List<String> vendors;
    transient String asian;
    transient String asianUpper;

    @BeforeEach
    void setup() {
        user = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        user11 = UUID.fromString("110e8400-e29b-41d4-a716-446655440000");
        address = new Address();
        address.setCity("Delft");
        address.setCity("Netherlands");
        address.setStreet("Mekelweg 5");
        address.setZip("2628CC");
        location = """
                {
                    "latitude": 51.998513,
                    "longitude": 4.37127
                },""";
        vendors = new ArrayList<>();
        vendors.add("""
                {
                  "userID": "550e8400-e29b-41d4-a716-446655440000",
                  "user": {
                    "id": "550e8400-e29b-41d4-a716-446655440000",
                    "firstname": "John",
                    "surname": "James",
                    "email": "john@email.com",
                    "avatar": "www.avatar.com/avatar.png",
                    "password": "12345",
                    "verified": false,
                    "userType": "Customer"
                  },
                  "cuisineType": "italian",
                  "location": {
                    "latitude": 51.998513,
                    "longitude": 4.37127
                  }
                }""");
        vendors.add("""
                {
                  "userID": "110e8400-e29b-41d4-a716-446655440000",
                  "user": {
                    "id": "550e8400-e29b-41d4-a716-446655440000",
                    "firstname": "John",
                    "surname": "James",
                    "email": "john@email.com",
                    "avatar": "www.avatar.com/avatar.png",
                    "password": "12345",
                    "verified": false,
                    "userType": "Customer"
                  },
                  "cuisineType": "asian",
                  "location": {
                    "latitude": 55.998513,
                    "longitude": 4.37127
                  }
                }""");
        asian = "asian";
        asianUpper = "Asian";
    }

    @Test
    void getAllRestaurantsVendorsNull() {
        when(mockUserService.getAllVendors()).thenReturn(null);

        assertThrows(RuntimeException.class, () -> restaurantService.getAllRestaurants(user));

        verify(mockUserService, times(1)).getAllVendors();
    }

    @Test
    void getAllRestaurantsVendorsEmpty() {
        when(mockUserService.getAllVendors()).thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> restaurantService.getAllRestaurants(user));

        verify(mockUserService, times(1)).getAllVendors();
    }

    @Test
    void getAllRestaurantsNoVendors() {
        when(mockUserService.getAllVendors()).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> restaurantService.getAllRestaurants(user));

        verify(mockUserService, times(1)).getAllVendors();
    }

    @Test
    void getAllRestaurantsVendorsEmptyVendors() throws UserIDNotFoundException {
        List<String> v1 = new ArrayList<>();
        v1.add("""
                {
                  "userID": "550e8400-e29b-41d4-a716-446655440000",
                  "user": {
                    "id": "550e8400-e29b-41d4-a716-446655440000",
                    "firstname": "John",
                    "surname": "James",
                    "email": "john@email.com",
                    "avatar": "www.avatar.com/avatar.png",
                    "password": "12345",
                    "verified": false,
                    "userType": "Customer"
                  },
                  "cuisineType": "italian"
                }""");
        when(mockUserService.getAllVendors()).thenReturn(v1);

        assertThrows(RuntimeException.class, () -> restaurantService.getAllRestaurants(user));
        verify(mockUserService, times(1)).getAllVendors();
    }

    @Test
    void getAllRestaurantsPartialVendors() throws UserIDNotFoundException {
        // userLocation setup
        when(mockUserService.getUserAddress(user)).thenReturn(address);
        when(mockLocationService.convertAddressToGeoCoords(address)).thenReturn(List.of(51.990013, 4.37127));

        when(mockUserService.getAllVendors()).thenReturn(vendors);

        List<UUID> result = restaurantService.getAllRestaurants(user);
        List<UUID> expected = List.of(user);
        // convert list to sets, because processVendors does not return list in an order
        Set<UUID> setResult = new HashSet<>(result);
        Set<UUID> setExpected = new HashSet<>(expected);


        verify(mockUserService, times(1)).getAllVendors();
        assertThat(setResult).isEqualTo(setExpected);
    }

    @Test
    void getAllRestaurantsAllVendors() throws UserIDNotFoundException {
        // userLocation setup
        when(mockUserService.getUserAddress(user)).thenReturn(address);
        when(mockLocationService.convertAddressToGeoCoords(address)).thenReturn(List.of(51.990013, 4.37127));
        List<String> vendorsAll = new ArrayList<>();
        vendorsAll.add("""
                {
                  "userID": "550e8400-e29b-41d4-a716-446655440000",
                  "user": {
                    "id": "550e8400-e29b-41d4-a716-446655440000",
                    "firstname": "John",
                    "surname": "James",
                    "email": "john@email.com",
                    "avatar": "www.avatar.com/avatar.png",
                    "password": "12345",
                    "verified": false,
                    "userType": "Customer"
                  },
                  "location": {
                    "latitude": 51.998513,
                    "longitude": 4.37127
                  }
                }""");
        vendorsAll.add("""
                {
                  "userID": "110e8400-e29b-41d4-a716-446655440000",
                  "user": {
                    "id": "110e8400-e29b-41d4-a716-446655440000",
                    "firstname": "John",
                    "surname": "James",
                    "email": "john@email.com",
                    "avatar": "www.avatar.com/avatar.png",
                    "password": "12345",
                    "verified": false,
                    "userType": "Customer"
                  },
                  "location": {
                    "latitude": 51.990513,
                    "longitude": 4.37127
                  }
                }""");

        when(mockUserService.getAllVendors()).thenReturn(vendorsAll);

        List<UUID> result = restaurantService.getAllRestaurants(user);
        List<UUID> expected = List.of(user,
                user11);
        // convert list to sets, because processVendors does not return list in an order
        Set<UUID> setResult = new HashSet<>(result);
        Set<UUID> setExpected = new HashSet<>(expected);


        verify(mockUserService, times(1)).getAllVendors();
        assertThat(setResult).isEqualTo(setExpected);
    }

    @Test
    void getAllRestaurantsNoVendorsMatch() throws UserIDNotFoundException {
        // userLocation setup
        when(mockUserService.getUserAddress(user)).thenReturn(address);
        when(mockLocationService.convertAddressToGeoCoords(address)).thenReturn(List.of(49.990013, 4.37127));

        when(mockUserService.getAllVendors()).thenReturn(vendors);

        List<UUID> result = restaurantService.getAllRestaurants(user);

        // convert list to sets, because processVendors does not return list in an order
        Set<UUID> setResult = new HashSet<>(result);

        verify(mockUserService, times(1)).getAllVendors();
        assertThat(setResult).isEmpty();
    }

    @Test
    void getUserAddressValid() throws UserIDNotFoundException {
        when(mockUserService.getUserAddress(user)).thenReturn(address);
        when(mockLocationService.convertAddressToGeoCoords(address)).thenReturn(List.of(51.998513, 4.37127));
        List<Double> expected = List.of(51.998513, 4.37127);
        List<Double> result = restaurantService.getUserLocation(user);

        verify(mockUserService, times(1)).getUserAddress(user);
        verify(mockLocationService, times(1)).convertAddressToGeoCoords(address);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getUserLocationValid() throws UserIDNotFoundException {
        when(mockUserService.getUserAddress(user)).thenThrow(UserIDNotFoundException.class);
        when(mockUserService.getUserLocation(user)).thenReturn(location);

        List<Double> expected = List.of(51.998513, 4.37127);
        List<Double> result = restaurantService.getUserLocation(user);

        verify(mockUserService, times(1)).getUserLocation(user);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getUserLocationJsonNull() throws UserIDNotFoundException {
        when(mockUserService.getUserAddress(user)).thenThrow(UserIDNotFoundException.class);
        when(mockUserService.getUserLocation(user)).thenReturn(null);

        assertThrows(UserIDNotFoundException.class, () -> restaurantService.getUserLocation(user));

        verify(mockUserService, times(1)).getUserLocation(user);
    }

    @Test
    void getUserLocationJsonEmpty() throws UserIDNotFoundException {
        when(mockUserService.getUserAddress(user)).thenThrow(UserIDNotFoundException.class);
        when(mockUserService.getUserLocation(user)).thenReturn("");

        assertThrows(UserIDNotFoundException.class, () -> restaurantService.getUserLocation(user));

        verify(mockUserService, times(1)).getUserLocation(user);
    }

    @Test
    void getUserLocationJsonParserFail() throws UserIDNotFoundException {
        when(mockUserService.getUserAddress(user)).thenThrow(UserIDNotFoundException.class);
        when(mockUserService.getUserLocation(user)).thenReturn("oh hi");

        assertThrows(UserIDNotFoundException.class, () -> restaurantService.getUserLocation(user));

        verify(mockUserService, times(1)).getUserLocation(user);
    }

    @Test
    void processVendorsValid() {
        List<Double> userLocation = Arrays.asList(52.001665, 4.373281);
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID uuid3 = UUID.randomUUID();
        HashMap<UUID, List<Double>> map = new HashMap<>();
        map.put(uuid1, List.of(52.001665, 4.373281));
        map.put(uuid2, List.of(52.1583, 4.4931));
        map.put(uuid3, List.of(52.011665, 4.373281));
        List<UUID> result  = restaurantService.processVendors(userLocation, map);
        List<UUID> expected = List.of(uuid1, uuid3);

        // convert to set for comparison, because stream doesn't process things in order
        Set<UUID> setResult = new HashSet<>(result);
        Set<UUID> setExpected = new HashSet<>(expected);
        assertThat(setResult).isEqualTo(setExpected);
    }

    @Test
    void processVendorsNoVendors() {
        List<Double> userLocation = Arrays.asList(52.001665, 4.373281);
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID uuid3 = UUID.randomUUID();
        HashMap<UUID, List<Double>> map = new HashMap<>();
        map.put(uuid1, List.of(50.001665, 4.373281));
        map.put(uuid2, List.of(54.1583, 4.4931));
        map.put(uuid3, List.of(53.011665, 4.373281));
        List<UUID> result  = restaurantService.processVendors(userLocation, map);

        assertThat(result).isEmpty();
    }

    @Test
    void calculateDistanceWithin100m() {
        double result = restaurantService.calculateDistance(52.001665, 4.373281, 52.001480, 4.372610);
        assertThat(result).isCloseTo(0.05, within(0.01));
    }

    @Test
    void calculateDistanceAround20km() {
        double result = restaurantService.calculateDistance(52.0067, 4.3556, 52.1583, 4.4931);
        assertThat(result).isCloseTo(19, within(1d));
    }

    @Test
    void processVendorsByQueryEmptyQuery() {
        HashMap<UUID, String> cuisines = new HashMap<>();
        UUID v1 = user11;
        UUID v2 = UUID.fromString("220e8400-e29b-41d4-a716-446655440000");
        cuisines.put(user, asianUpper);
        cuisines.put(v1, "");
        cuisines.put(v2, "italian");
        List<UUID> result = restaurantService.processVendorsByQuery(cuisines, "");
        List<UUID> expected = List.of(user, v1, v2);

        Set<UUID> setResult = new HashSet<>(result);
        Set<UUID> setExpected = new HashSet<>(expected);

        assertThat(setResult).isEqualTo(setExpected);
    }

    @Test
    void processVendorsByQueryPartialVendors() {
        HashMap<UUID, String> cuisines = new HashMap<>();
        UUID v1 = user11;
        UUID v2 = UUID.fromString("220e8400-e29b-41d4-a716-446655440000");
        cuisines.put(user, asianUpper);
        cuisines.put(v1, "");
        cuisines.put(v2, "italian");
        List<UUID> result = restaurantService.processVendorsByQuery(cuisines, "ian");
        List<UUID> expected = new ArrayList<>(List.of(user, v2));

        Set<UUID> setResult = new HashSet<>(result);
        Set<UUID> setExpected = new HashSet<>(expected);

        assertThat(setResult).isEqualTo(setExpected);
    }

    @Test
    void getAllRestaurantsWithQueryEmptyVendorsError() throws UserIDNotFoundException {
        // userLocation setup
        when(mockUserService.getUserAddress(user)).thenReturn(address);
        when(mockLocationService.convertAddressToGeoCoords(address)).thenReturn(List.of(51.990013, 4.37127));

        when(mockUserService.getAllVendors()).thenReturn(vendors);
        when(mockUserService.getVendorsFromID(anyList())).thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> restaurantService.getAllRestaurantsWithQuery(user,  "query"));
        verify(mockUserService, times(1)).getVendorsFromID(anyList());
    }

    @Test
    void getAllRestaurantsWithQueryUserError() throws UserIDNotFoundException {
        when(mockUserService.getAllVendors()).thenReturn(vendors);
        when(mockUserService.getUserAddress(user)).thenThrow(UserIDNotFoundException.class);
        when(mockUserService.getUserLocation(user)).thenReturn(null);


        assertThrows(RuntimeException.class, () -> restaurantService.getAllRestaurantsWithQuery(user, "query"));
        verify(mockUserService, times(1)).getAllVendors();
        verify(mockUserService, times(1)).getUserAddress(user);
        verify(mockUserService, times(1)).getUserLocation(user);
        verify(mockUserService, times(0)).getVendorsFromID(anyList());
    }

    @Test
    void getAllRestaurantsWithQueryPartialVendorMatch() throws UserIDNotFoundException {
        UUID v1 = user11;

        // userLocation setup
        when(mockUserService.getUserAddress(user)).thenReturn(address);
        when(mockLocationService.convertAddressToGeoCoords(address)).thenReturn(List.of(51.990013, 4.37127));

        when(mockUserService.getAllVendors()).thenReturn(vendors);
        when(mockUserService.getVendorsFromID(anyList())).thenReturn(vendors);

        List<UUID> result = restaurantService.getAllRestaurantsWithQuery(user, asian);
        List<UUID> expected = List.of(v1);

        Set<UUID> setResult = new HashSet<>(result);
        Set<UUID> setExpected = new HashSet<>(expected);

        verify(mockUserService, times(1)).getVendorsFromID(anyList());
        assertThat(setResult).isEqualTo(setExpected);
    }
}