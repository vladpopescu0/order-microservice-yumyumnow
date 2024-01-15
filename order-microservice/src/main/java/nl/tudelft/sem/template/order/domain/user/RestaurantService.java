package nl.tudelft.sem.template.order.domain.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.model.Address;
import nl.tudelft.sem.template.user.services.JsonParserService;
import nl.tudelft.sem.template.user.services.MockLocationService;
import nl.tudelft.sem.template.user.services.UserMicroServiceService;
import org.springframework.stereotype.Service;

/**
 * The type Restaurant service.
 */
@Service
public class RestaurantService {

    private final transient UserMicroServiceService userMicroServiceService;
    private final transient MockLocationService mockedLocationService;


    /**
     * Instantiates a new Restaurant service.
     *
     * @param userMicroServiceService the user microservice service
     * @param mockedLocationService mocked location service
     */
    public RestaurantService(UserMicroServiceService userMicroServiceService, MockLocationService mockedLocationService) {
        this.userMicroServiceService = userMicroServiceService;
        this.mockedLocationService = mockedLocationService;
    }

    /**
     *  Gets all restaurants.
     *  Algorithm:
     *  Get userLocation based on ID,
     *  Get all the vendors,
     *  filter the vendors on distance from the user
     *
     * @param userID the user id of the customer
     * @return list of UUID from the vendors in a specific radius
     * @throws UserIDNotFoundException the user id not found exception
     * @throws RuntimeException in case of other exceptions, just throw RunTimeException
     */
    public List<UUID> getAllRestaurants(UUID userID) throws UserIDNotFoundException, RuntimeException {
        // try to get the user address first

        // get vendor location and UUID
        try {
            List<String> jsonVendors = userMicroServiceService.getAllVendors();
            HashMap<UUID, List<Double>> vendors = JsonParserService.parseVendorsLocation(jsonVendors);
            // option: error thrown in the JsonParserService could be caught in this try catch
            if (vendors == null || vendors.isEmpty()) {
                throw new RuntimeException("Something went wrong parsing vendors");
            }
            // Get the vendor UUID nearby the customer
            List<Double> userLocation = getUserLocation(userID);
            return processVendors(userLocation, vendors);
        } catch (Exception e) {
            throw new RuntimeException("Could not get vendors");
        }
    }

    /**
     * getter for the location of the user.
     *
     * @param userID UUID of the user
     * @return List of doubles representing the longitude and latitude
     * @throws UserIDNotFoundException if the user is not found
     */
    public List<Double> getUserLocation(UUID userID) throws UserIDNotFoundException {
        try {
            Address userAddress = userMicroServiceService.getUserAddress(userID);
            // this always returns the geo coordinates of TU Aula, unless we catch an error
            return mockedLocationService.convertAddressToGeoCoords(userAddress);
        } catch (UserIDNotFoundException e) {

            // if we catch an error, then get the user's current location
            try {
                String jsonUser = userMicroServiceService.getUserLocation(userID);
                if (jsonUser == null || jsonUser.isEmpty()) { // in case getUserLocation timed out.
                    throw new UserIDNotFoundException(userID);
                }
                List<Double> userLocation = JsonParserService.parseLocation(jsonUser);
                // option: error thrown in the JsonParserService could be caught in this try catch
                if (userLocation == null) {
                    throw new RuntimeException("Something went wrong parsing location");
                }
                return userLocation;
            } catch (Exception ex) {
                throw new UserIDNotFoundException(userID);
            }
        }
    }

    /**
     * Filters vendors based on their proximity to the customer's location within a specified radius.
     *
     * @param userLocation the location of the user
     * @param vendors vendors that need to be filtered
     * @return a list of vendor UUIDs
     */
    public List<UUID> processVendors(List<Double> userLocation, HashMap<UUID, List<Double>> vendors) {
        return vendors.entrySet().stream()
                .filter(entry -> calculateDistance(userLocation.get(0), userLocation.get(1),
                        entry.getValue().get(0), entry.getValue().get(1)) < 5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Calculate distance of two points (geoCoordinates).
     * Algorithm found at:
     * <a href="https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula">...</a>
     *
     * @param userLatitude latitude of user
     * @param userLongitude longitude of user
     * @param vendorLatitude latitude of vendor
     * @param vendorLongitude longitude of vendor
     * @return the distance between two geo coordinate points
     */
    public double calculateDistance(double userLatitude, double userLongitude,
                                    double vendorLatitude, double vendorLongitude) {
        // Calculate distance between two points using Haversine formula
        final double r = 6371D; // radius of Earth in km
        final double p = Math.PI / 180;

        double a = 0.5 - Math.cos((vendorLatitude - userLatitude) * p) / 2
                + Math.cos(userLatitude * p) * Math.cos(vendorLatitude * p)
                * (1 - Math.cos((vendorLongitude - userLongitude) * p)) / 2;
        return 2 * r * Math.asin(Math.sqrt(a));
    }
}
