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
        if(json == null || json.isEmpty()){
            return null;
        }
        try{
            List<Double> result = new ArrayList<>(2);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            Double latitude = (jsonNode.get("latitude").isDouble()) ? jsonNode.get("latitude").asDouble(): null;
            Double longitude = (jsonNode.get("longitude").isDouble())? jsonNode.get("longitude").asDouble(): null;
            if(latitude == null || longitude == null){
                return null;
            }
            result.add(latitude);
            result.add(longitude);
            return result;
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
            ObjectMapper objectMapper = new ObjectMapper();
            for(String jsonVendor: jsonVendors){
                JsonNode jsonNode = objectMapper.readTree(jsonVendor);
                JsonNode locationNode = jsonNode.get("location");
                String loc = (locationNode == null)? null: locationNode.toString();
                List<Double> location = parseLocation(loc);
                UUID uuid;
                try{
                    uuid = UUID.fromString(jsonNode.get("userID").asText());
                } catch (IllegalArgumentException e){
                    break;
                }
                if(location == null){
                    break;
                }
                result.put(uuid,location);
            }
            return result;
        } catch (JsonProcessingException e){
            return null;
        }
    }
}
