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
        try{
            List<Double> result = new ArrayList<>(2);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            Double latitude = jsonNode.get("latitude").asDouble();
            Double longitude = jsonNode.get("longitude").asDouble();
            result.add(latitude);
            result.add(longitude);
            return result;
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static HashMap<UUID, List<Double>> parseVendorsLocation(List<String> jsonVendors) {
        try{
            HashMap<UUID, List<Double>> result = new HashMap<>();
            ObjectMapper objectMapper = new ObjectMapper();
            for(String jsonVendor: jsonVendors){
                List<Double> location = parseLocation(jsonVendor);
                JsonNode jsonNode = objectMapper.readTree(jsonVendor);
                UUID uuid = UUID.fromString(jsonNode.get("userID").asText());
                result.put(uuid,location);
            }
            return result;
        } catch (JsonProcessingException e){
            return null;
        }
    }
}
