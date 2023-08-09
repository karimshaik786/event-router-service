package org.bahmni.eventrouterservice.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public abstract class PropertiesFilter {

    private final ObjectMapper objectMapper;

    public PropertiesFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public boolean matches(String jsonPayloadAsString, LinkedHashMap<String, String> listOfFilters) {
        boolean matches = true;

        try {
            JsonNode payload = objectMapper.readValue(jsonPayloadAsString, JsonNode.class);
            for (Map.Entry<String, String> filterCondition : listOfFilters.entrySet()) {
                matches = matches
                        && payload.findValue(filterCondition.getKey()) != null
                        && payload.findValues(filterCondition.getKey()).stream().anyMatch(node -> node.asText("").equals(filterCondition.getValue()));
            }
            log.info("Filters conditions matches : " + matches +" for uuid : "+payload.get("uuid"));
            return matches;
        } catch (JsonProcessingException exception) {
            log.info("Failed to process payload : " + exception.getMessage());
            throw new RuntimeException(exception);
        }
    }
}
