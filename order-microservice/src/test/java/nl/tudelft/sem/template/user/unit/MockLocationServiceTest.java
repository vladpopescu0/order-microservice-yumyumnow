package nl.tudelft.sem.template.user.unit;

import nl.tudelft.sem.template.model.Address;
import nl.tudelft.sem.template.user.services.MockLocationService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MockLocationServiceTest {

    @Test
    void convertAddressToGeoCoords() {
        MockLocationService mockLocationService = new MockLocationService();
        List<Double> list = Arrays.asList(52.0021256d,4.3732982d);
        List<Double> result = mockLocationService.convertAddressToGeoCoords(new Address());
        assertEquals(list,result);
    }

    @Test
    void convertAddressToGeoCoordsNull() {
        MockLocationService mockLocationService = new MockLocationService();
        List<Double> list = Arrays.asList(52.0021256d,4.3732982d);
        List<Double> result = mockLocationService.convertAddressToGeoCoords(null);
        assertEquals(list,result);
    }
}