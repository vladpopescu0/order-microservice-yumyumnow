package nl.tudelft.sem.template.user.services;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.model.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockLocationService {



    /**
     * Instantiates a new UserMicroService service.
     *
     */
    @Autowired
    public MockLocationService() {
    }


    /**
     * Convert address to geo coords list.
     *
     * @param address the address
     * @return the list
     */
    public List<Double> convertAddressToGeoCoords(Address address) {
        System.out.println(address);
        List<Double> list = new ArrayList<>(2);
        list.add(52.0021256d);
        list.add(4.3732982d);
        return list;
    }


}
