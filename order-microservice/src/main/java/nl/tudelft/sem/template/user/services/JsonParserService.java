package nl.tudelft.sem.template.user.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class JsonParserService {

    public static List<Double> parseLocation(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            Double latitude = (jsonNode.get("latitude").isDouble()) ? jsonNode.get("latitude").asDouble(): null;
            Double longitude = (jsonNode.get("longitude").isDouble())? jsonNode.get("longitude").asDouble(): null;
            if(latitude == null || longitude == null){
                return null;
            }
            return new ArrayList<>(List.of(latitude, longitude));
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static HashMap<UUID, List<Double>> parseVendorsLocation(List<String> jsonVendors) {
        if(jsonVendors == null || jsonVendors.isEmpty()){
            return null;
        }
        try{
            HashMap<UUID, List<Double>> result = new HashMap<>();
            for(String jsonVendor: jsonVendors) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(jsonVendor);
                JsonNode locationNode = jsonNode.get("location");
                String loc = (locationNode == null)? null: locationNode.toString();
                List<Double> location = parseLocation(loc);
                if(location == null){
                    break;
                }
                try {
                    UUID uuid = UUID.fromString(jsonNode.get("userID").asText());
                    result.put(uuid,location);
                } catch (IllegalArgumentException e){
                    break;
                }
            }
            return result;
        } catch (JsonProcessingException e){
            return null;
        }
    }
}
