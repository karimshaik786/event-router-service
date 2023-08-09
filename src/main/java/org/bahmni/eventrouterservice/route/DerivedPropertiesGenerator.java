package org.bahmni.eventrouterservice.route;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bahmni.eventrouterservice.configuration.RouteDescriptionLoader.RouteDescription;

import java.util.Map;

@Slf4j
class DerivedPropertiesGenerator implements Processor {

    private final RouteDescription routeDescription;

    public DerivedPropertiesGenerator(RouteDescription routeDescription) {
        this.routeDescription = routeDescription;
    }

    @Override
    public void process(Exchange exchange) {
        if(routeDescription.getDerivedProperties().isEmpty()) {
            log.info("Empty Derived Properties ");
            return;
        }

        String payload = exchange.getIn().getBody(String.class);

        for (Map.Entry<String, String> derivedProperty : routeDescription.getDerivedProperties().entrySet()) {
            Object value = null;
            try {
                value = JsonPath.read(payload, derivedProperty.getValue());
                log.info("Derived property set : " + derivedProperty.getKey() + " : " + value);
            } catch (Exception ex) {
                log.error("Derived properties set to null for key : " + derivedProperty.getKey() + " and value : " + derivedProperty.getValue());
            }
            exchange.setProperty(derivedProperty.getKey(), value);
        }
    }
}