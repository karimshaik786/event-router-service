package org.bahmni.eventrouterservice.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.bahmni.eventrouterservice.configuration.RouteDescriptionLoader.RouteDescription;

@Slf4j
class EventPropertiesFilter extends PropertiesFilter implements Predicate {
    private final RouteDescription routeDescription;

    public EventPropertiesFilter(ObjectMapper objectMapper, RouteDescription routeDescription) {
        super(objectMapper);
        this.routeDescription = routeDescription;
    }

    @Override
    public boolean matches(Exchange exchange) {
        log.info("Checking Event Filters conditions : " + routeDescription.getFilterBy().getEventProperties());
        if(routeDescription.getFilterBy().getEventProperties().isEmpty()) {
            log.info("Empty Event Filters conditions");
            return true;
        }

        String eventPayloadAsJson = exchange.getIn().getBody(String.class);
        return super.matches(eventPayloadAsJson, routeDescription.getFilterBy().getEventProperties());
    }
}