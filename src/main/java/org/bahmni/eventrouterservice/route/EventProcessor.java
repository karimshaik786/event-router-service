package org.bahmni.eventrouterservice.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bahmni.eventrouterservice.configuration.RouteDescriptionLoader.RouteDescription;
import org.bahmni.eventrouterservice.configuration.RouteDescriptionLoader.AdditionalProperty;
import org.bahmni.eventrouterservice.configuration.RouteDescriptionLoader.SplitPatternConfiguration;

import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
class EventProcessor implements Processor {

    private final RouteDescription routeDescription;

    public EventProcessor(RouteDescription routeDescription) {
        this.routeDescription = routeDescription;
    }

    @Override
    public void process(Exchange exchange) {
        if(routeDescription.getAdditionalProperties().isEmpty()) {
            log.info("Empty additional properties");
            return;
        }
        String payloadAsJsonString = exchange.getIn().getBody(String.class);
        String updatedPayloadAsJson = addAdditionalProperties(payloadAsJsonString, routeDescription.getAdditionalProperties());
        exchange.getIn().setBody(updatedPayloadAsJson);

        String destinationTopic = getDestination(exchange.getIn().getHeader("eventType"));
        exchange.setProperty("destination", destinationTopic);
    }

    private String getDestination(Object eventType) {
        String eventTypeAsString = (String) eventType;
        return routeDescription.getDestinationBasedOn(eventTypeAsString).getTopic().getName();
    }

    private String addAdditionalProperties(String payloadAsJsonString, List<AdditionalProperty> additionalProperties) {
        try {
            DocumentContext[] contextRef = {JsonPath.parse(payloadAsJsonString)};

            for (AdditionalProperty obj : additionalProperties) {
                String parentPath = obj.getParentPath();
                String filterKeyPath = obj.getFilterKeyPath();
                String filterValue = obj.getFilterValue();
                LinkedHashMap<String, SplitPatternConfiguration> splitPatternConfigurations = obj.getSplitPatternConfigurations();
                if (obj.getStaticProperties() != null && obj.getStaticProperties().size() > 0) {
                    obj.getStaticProperties().entrySet().forEach(entry -> {
                        contextRef[0].put(JsonPath.compile(parentPath), entry.getKey(), entry.getValue());
                    });
                }

                if (obj.getDynamicProperties() != null && obj.getDynamicProperties().size() > 0) {
                    obj.getDynamicProperties().entrySet().forEach(entry -> {
                        Object parentObj = JsonPath.read(payloadAsJsonString, parentPath);
                        if (parentObj instanceof List && !((List<?>) parentObj).isEmpty()) {
                            contextRef[0] = contextRef[0].map(parentPath, (currentValue, configuration) -> {
                                Object filterObj = JsonPath.read(currentValue, filterKeyPath);
                                Object valueObj = JsonPath.read(currentValue, entry.getValue());
                                DocumentContext obsContext = JsonPath.parse(currentValue);
                                if (splitPatternConfigurations != null && splitPatternConfigurations.get(entry.getKey()) != null) {
                                    valueObj = applySplitPattern(valueObj, splitPatternConfigurations.get(entry.getKey()));
                                } 
                                if ((filterKeyPath == null && filterValue == null) || (filterObj != null && filterObj.toString().contains(filterValue))) {
                                    obsContext.put(JsonPath.compile("$"), entry.getKey(), valueObj);
                                }
                                return obsContext.json();
                            });
                        }
                    });
                }
            }
            return contextRef[0].jsonString();
        } catch (Exception exception) {
            log.info("Failed to process payload for additional properties : " + exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    private Object applySplitPattern(Object value, SplitPatternConfiguration splitConfig) {
        if (value instanceof String) {
            String[] parts = ((String) value).split(splitConfig.getSplitPattern());
            Integer index = splitConfig.getSplitIndex();
            if (index >= 0 && index < parts.length) {
                return parts[index];
            }
        }
        return value;
    }

}
