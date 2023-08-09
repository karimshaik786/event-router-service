package org.bahmni.eventrouterservice.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.routepolicy.quartz.CronScheduledRoutePolicy;
import org.bahmni.eventrouterservice.configuration.RouteDescriptionLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static org.apache.camel.LoggingLevel.ERROR;
import static org.apache.camel.LoggingLevel.INFO;

@Component
@ConditionalOnProperty(name = "bahmni.activemqToGCP.failed-route-enabled", havingValue = "true")
public class BahmniActiveMQToGCPTopicFailedRoute extends RouteBuilder {

    private final RouteDescriptionLoader routeDescriptionLoader;
    private final String googlePubSubProjectId;
    private final ObjectMapper objectMapper;
    private final BahmniAPIGateway bahmniAPIGateway;

    public BahmniActiveMQToGCPTopicFailedRoute(CamelContext context,
                                               RouteDescriptionLoader routeDescriptionLoader,
                                               @Value("${google-pubsub.project-id}") String googlePubSubProjectId,
                                               ObjectMapper objectMapper,
                                               BahmniAPIGateway bahmniAPIGateway) {
        super(context);
        this.routeDescriptionLoader = routeDescriptionLoader;
        this.googlePubSubProjectId = googlePubSubProjectId;
        this.objectMapper = objectMapper;
        this.bahmniAPIGateway = bahmniAPIGateway;
    }

    @Override
    public void configure() {

        System.out.println("googlePubSubProjectId : "+googlePubSubProjectId);

        routeDescriptionLoader.getRouteDescriptions().forEach(routeDescription -> {

            CronScheduledRoutePolicy cronScheduledRoutePolicy = new CronScheduledRoutePolicy();
            cronScheduledRoutePolicy.setRouteStartTime(routeDescription.getErrorDestination().getCronExpressionForRetryStart());
            cronScheduledRoutePolicy.setRouteStopTime(routeDescription.getErrorDestination().getCronExpressionForRetryStop());

            EventPropertiesFilter eventPropertiesFilter = new EventPropertiesFilter(objectMapper, routeDescription);
            PatientPropertiesFilter patientPropertiesFilter = new PatientPropertiesFilter(objectMapper, routeDescription, bahmniAPIGateway);
            EventProcessor eventProcessor = new EventProcessor(objectMapper, routeDescription);
            DerivedPropertiesGenerator derivedPropertiesGenerator = new DerivedPropertiesGenerator(routeDescription);

            from("activemq:queue:" + routeDescription.getErrorDestination().getQueue().getName())
                .routePolicy(cronScheduledRoutePolicy)
                .noAutoStartup()
                .onException(Exception.class)
                    .handled(true)
                    .log(ERROR, "Following exception occurred : ${exception.message} for processing the payload : ${body}")
                .end()
                .log(INFO, "Received failed message from ActiveMQ queue : " + routeDescription.getErrorDestination().getQueue().getName())
                .filter(eventPropertiesFilter)
                .process(derivedPropertiesGenerator)
                .filter(patientPropertiesFilter)
                .process(eventProcessor)
                .toD("google-pubsub:"+googlePubSubProjectId+":"+"${headers.destination}")
                .log("Message sent to Google PubSub on topic : "+"${headers.destination}");
        });
    }
}
