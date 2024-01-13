package nl.tudelft.sem.template.order.domain.user;

import nl.tudelft.sem.template.order.commons.Address;
import nl.tudelft.sem.template.user.services.JsonParserService;
import nl.tudelft.sem.template.user.services.MockLocationService;
import nl.tudelft.sem.template.user.services.UserMicroServiceService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


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
     * @param mockedLocationService, mocked location service
     */
    public RestaurantService(UserMicroServiceService userMicroServiceService, MockLocationService mockedLocationService) {
        this.userMicroServiceService = userMicroServiceService;
        this.mockedLocationService = mockedLocationService;
    }

    /**
     * Gets all restaurants.
     * Algorithm:
     *  Get userLocation based on ID,
     *  Get all the vendors,
     *  filter the vendors on distance from the user
     *
     * @param userID the user id of the customer
     * @return list of UUID from the vendors in a specific radius
     * @throws UserIDNotFoundException the user id not found exception
     * @throws RuntimeException in case of other exceptions, just throw RunTimeException
     */
    public List<UUID> getAllRestaurants(UUID userID) throws UserIDNotFoundException, RuntimeException{
        List<Double> userLocation;
        HashMap<UUID, List<Double>> vendors;
        // try to get the user address first
        try{
            Address userAddress = userMicroServiceService.getUserAddress(userID);
            // this always returns the geo coordinates of TU Aula
            userLocation = mockedLocationService.convertAddressToGeoCoords(userAddress);
        } catch (UserIDNotFoundException e){

            // if it fails, then get the user's current location
            try{
                String jsonUser = userMicroServiceService.getUserLocation(userID);
                if(jsonUser == null || jsonUser.isEmpty()){ // in case getUserLocation timed out.
                    throw new UserIDNotFoundException(userID);
                }
                userLocation = JsonParserService.parseLocation(jsonUser);
                if(userLocation == null){ // option: error thrown in the JsonParserService could be caught in this try catch
                    throw new RuntimeException("Something went wrong parsing location");
                }
            } catch (Exception ex){
                throw new UserIDNotFoundException(userID);
            }
        }

        // get vendor location and UUID
        try{
            List<String> jsonVendors = userMicroServiceService.getAllVendors();
            if(jsonVendors == null || jsonVendors.isEmpty()){
                throw new RuntimeException("Could not get vendors");
            }
             vendors = JsonParserService.parseVendorsLocation(jsonVendors);
            if(vendors == null){ // option: error thrown in the JsonParserService could be caught in this try catch
                throw new RuntimeException("Something went wrong parsing vendors");
            }
        } catch (Exception e){
            throw new RuntimeException("Could not get vendors");
        }
        // Get the vendor UUID nearby the customer
        return processVendors(userLocation, vendors);
    }

    /**
     * Filters vendors based on their proximity to the customer's location within a specified radius.
     *
     * @param userLocation, the location of the user
     * @param vendors, vendors that need to be filtered
     * @return a list of vendor UUIDs
     */
    public List<UUID> processVendors(List<Double> userLocation,HashMap<UUID, List<Double>> vendors) {
        double radius = 5; //in km
        return vendors.entrySet().stream()
                .filter(entry -> calculateDistance(userLocation, entry.getValue()) < radius)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Calculate distance of two points (geoCoordinates)
     * Algorithm found at:
     * <a href="https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula">...</a>
     *
     * @param userLocation the user location
     * @param vendorLocation geo coordinate of the vendor
     * @return the distance between two geo coordinate points
     */
    public double calculateDistance(List<Double> userLocation, List<Double> vendorLocation) {
        double userLatitude = userLocation.get(0);
        double userLongitude = userLocation.get(1);
        double vendorLatitude = vendorLocation.get(0);
        double vendorLongitude = vendorLocation.get(0);

        // Calculate distance between two points using Haversine formula
        final double r = 6371D; // radius of Earth in km
        final double p = Math.PI / 180;

        double a = 0.5 - Math.cos((vendorLatitude - userLatitude) * p) / 2
                + Math.cos(userLatitude) * p *Math.cos(vendorLatitude * p) *
                (1 - Math.cos((vendorLongitude - userLongitude) * p)) / 2;
        return 2 * r * Math.asin(Math.sqrt(a));
    }
}
