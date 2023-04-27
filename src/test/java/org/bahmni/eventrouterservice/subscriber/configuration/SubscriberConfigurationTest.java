package org.bahmni.eventrouterservice.subscriber.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bahmni.eventrouterservice.configuration.ServiceName;
import org.bahmni.eventrouterservice.exception.FailedToLoadConfiguration;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SubscriberConfigurationTest {

    @Test
    public void givenConfigurationFile_whenInstantiating_thenLoadSubscriberConfiguration() {

        String subscriberDescriptionConfigurationPath = "src/test/resources/subscriber-description-configuration.json";
        String subscriberScheduleConfigurationPath = "src/test/resources/subscriber-schedule-configuration.json";
        new SubscriberConfiguration(subscriberDescriptionConfigurationPath, subscriberScheduleConfigurationPath, new ObjectMapper());
    }

    @Test
    public void givenInvalidDescriptionConfigurationFile_whenInstantiating_thenThrowException() {

        String subscriberDescriptionConfigurationPath = "src/test/resources/subscriber-description-configuration-invalid.json";
        String subscriberScheduleConfigurationPath = "src/test/resources/subscriber-schedule-configuration.json";

        FailedToLoadConfiguration exception = assertThrows(FailedToLoadConfiguration.class,
                () ->  new SubscriberConfiguration(subscriberDescriptionConfigurationPath, subscriberScheduleConfigurationPath, new ObjectMapper()));

        assertEquals("Failed to load configuration file : src/test/resources/subscriber-description-configuration-invalid.json", exception.getMessage());
    }

    @Test
    public void givenInvalidScheduleConfigurationFile_whenInstantiating_thenThrowException() {

        String subscriberDescriptionConfigurationPath = "src/test/resources/subscriber-description-configuration.json";
        String subscriberScheduleConfigurationInvalidPath = "src/test/resources/subscriber-schedule-configuration-invalid.json";

        FailedToLoadConfiguration exception = assertThrows(FailedToLoadConfiguration.class,
                () ->  new SubscriberConfiguration(subscriberDescriptionConfigurationPath, subscriberScheduleConfigurationInvalidPath, new ObjectMapper()));

        assertEquals("Failed to load configuration file : src/test/resources/subscriber-schedule-configuration-invalid.json", exception.getMessage());
    }

    @Test
    public void givenConfigurationFile_whenGettingOrderedSubscription_thenReturnOrderSubscriptionConfiguration() {

        String subscriberDescriptionConfigurationPath = "src/test/resources/subscriber-description-configuration.json";
        String subscriberScheduleConfigurationPath = "src/test/resources/subscriber-schedule-configuration.json";
        SubscriberConfiguration subscriberConfiguration = new SubscriberConfiguration(subscriberDescriptionConfigurationPath, subscriberScheduleConfigurationPath, new ObjectMapper());

        List<SubscriberConfiguration.SubscriberDescription> orderedSubscription = subscriberConfiguration.getSubscribersAsPerOrderOfSubscriptionFor(ServiceName.BAHMNI);

        assertEquals(2, orderedSubscription.size());
        assertEquals(1, orderedSubscription.get(0).getOrderOfSubscription());
        assertEquals(2, orderedSubscription.get(1).getOrderOfSubscription());
    }
}