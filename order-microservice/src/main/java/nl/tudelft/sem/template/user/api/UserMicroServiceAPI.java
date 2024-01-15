package nl.tudelft.sem.template.user.api;

import nl.tudelft.sem.template.model.Address;
import nl.tudelft.sem.template.order.domain.user.UserIDNotFoundException;

import java.util.List;
import java.util.UUID;

public interface UserMicroServiceAPI {

    Address getUserAddress(UUID userID) throws UserIDNotFoundException;
    String getUserLocation(UUID userID) throws UserIDNotFoundException;
    List<String> getAllVendors();
}
