package nl.tudelft.sem.template.user.unit;

import nl.tudelft.sem.template.user.services.JsonParserService;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JsonParserServiceTest {

    String jsonValid = """
            {
              "latitude": 51.998513,
              "longitude": 4.37127
            }""";
    String jsonNoLat = """
            {
              "latitude": ,
              "longitude": 4.37127
            }""";
    String jsonNoLong = """
            {
              "latitude": 51.998513,
              "longitude":
            }""";
    String jsonString = """
            {
              "latitude": oh hi,
              "longitude": 4.37127
            }""";
    String jsonExtraAttr = """
            {
              "latitude": 51.998513,
              "longitude": 4.37127,
              "angle": 30
            }""";
    List<String> vendor2 = Arrays.asList("""
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
                "address": {
                  "street": "437 Lytton",
                  "city": "Palo Alto",
                  "country": "Germany",
                  "zip": "2511RE"
                },
                "location": {
                  "latitude": 51.998513,
                  "longitude": 4.37127
                }
              }
            """, """
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
                "address": {
                  "street": "437 Lytton",
                  "city": "Palo Alto",
                  "country": "Germany",
                  "zip": "2511RE"
                },
                "location": {
                  "latitude": 5.998513,
                  "longitude": 41.37127
                }
            }""");
    List<String> vendor2NotValid = Arrays.asList("""
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
                "address": {
                  "street": "437 Lytton",
                  "city": "Palo Alto",
                  "country": "Germany",
                  "zip": "2511RE"
                },
                "location": {
                  "latitude": 51.998513,
                  "longitude": 4.37127
                }
              }
            """, """
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
                "address": {
                  "street": "437 Lytton",
                  "city": "Palo Alto",
                  "country": "Germany",
                  "zip": "2511RE"
                }
                
            }""");
    List<String> vendor2NoLoc = Arrays.asList("""
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
                "address": {
                  "street": "437 Lytton",
                  "city": "Palo Alto",
                  "country": "Germany",
                  "zip": "2511RE"
                },
                "location": {
                  "latitude": 51.998513,
                  "longitude": 4.37127
                }
              }
            """, """
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
                "address": {
                  "street": "437 Lytton",
                  "city": "Palo Alto",
                  "country": "Germany",
                  "zip": "2511RE"
                },
                "location": {
                  "latitude": 5.998513,
                  "longitude": "oh hi"
                }
            }""");
    List<String> vendor1InValidUUID = List.of("""
              {
                "userID": "oh hi",
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
                "address": {
                  "street": "437 Lytton",
                  "city": "Palo Alto",
                  "country": "Germany",
                  "zip": "2511RE"
                },
                "location": {
                  "latitude": 51.998513,
                  "longitude": 4.37127
                }
              }
            """);
    List<String> vendorInvalidJson = List.of("""
              {
                userID : "oh hi",
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
                "address": {
                  "street": "437 Lytton",
                  "city": "Palo Alto",
                  "country": "Germany",
                  "zip": "2511RE"
                },
                "location": {
                  "latitude": 51.998513,
                  "longitude": 4.37127
                }
              }
            """);

    @Test
    void parseLocationValid() {
        List<Double> list = Arrays.asList(51.998513, 4.37127);
        List<Double> result = JsonParserService.parseLocation(jsonValid);
        assertEquals(list,result);
    }

    @Test
    void parseLocationNoLat() {
        List<Double> result = JsonParserService.parseLocation(jsonNoLat);
        assertNull(result);
    }

    @Test
    void parseLocationNoLong() {
        List<Double> result = JsonParserService.parseLocation(jsonNoLong);
        assertNull(result);
    }

    @Test
    void parseLocationString() {
        List<Double> result = JsonParserService.parseLocation(jsonString);
        assertNull(result);
    }

    @Test
    void parseLocationExtraAttr() {
        List<Double> list = Arrays.asList(51.998513, 4.37127);
        List<Double> result = JsonParserService.parseLocation(jsonExtraAttr);
        assertEquals(list,result);
    }
    

    @Test
    void parseVendorsLocationEmpty() {
        HashMap<UUID, List<Double>> result = JsonParserService.parseVendorsLocation(new ArrayList<>());
        assertNull(result);
    }

    @Test
    void parseVendorsLocationValid() {
        HashMap<UUID, List<Double>> hashMap = new HashMap<>();
        hashMap.put(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"), Arrays.asList(51.998513, 4.37127));
        hashMap.put(UUID.fromString("110e8400-e29b-41d4-a716-446655440000"), Arrays.asList(5.998513, 41.37127));
        HashMap<UUID, List<Double>> result = JsonParserService.parseVendorsLocation(vendor2);
        assertEquals(hashMap, result);
    }

    @Test
    void parseVendorsLocationNoLocation() {
        HashMap<UUID, List<Double>> hashMap = new HashMap<>();
        hashMap.put(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"), Arrays.asList(51.998513, 4.37127));
        HashMap<UUID, List<Double>> result = JsonParserService.parseVendorsLocation(vendor2NotValid);
        assertEquals(hashMap, result);
    }

    @Test
    void parseVendorsLocationMissingLocation() {
        HashMap<UUID, List<Double>> hashMap = new HashMap<>();
        hashMap.put(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"), Arrays.asList(51.998513, 4.37127));
        HashMap<UUID, List<Double>> result = JsonParserService.parseVendorsLocation(vendor2NoLoc);
        assertEquals(hashMap, result);
    }

    @Test
    void parseVendorsLocationInvalidUUID() {
        HashMap<UUID, List<Double>> hashMap = new HashMap<>();
        HashMap<UUID, List<Double>> result = JsonParserService.parseVendorsLocation(vendor1InValidUUID);
        assertEquals(hashMap, result);
    }

    @Test
    void parseVendorsLocationInvalidJson() {
        HashMap<UUID, List<Double>> result = JsonParserService.parseVendorsLocation(vendorInvalidJson);
        assertNull(result);
    }



}