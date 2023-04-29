package org.bahmni.eventrouterservice.publisher.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bahmni.eventrouterservice.exception.FailedToLoadConfiguration;
import org.bahmni.eventrouterservice.publisher.exception.EndpointNotConfiguredForPublisherException;
import org.bahmni.eventrouterservice.publisher.exception.TopicNotConfiguredForPublisherException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PublisherConfigurationTest {

    @Test
    public void givenConfigurationFile_whenInstantiating_thenLoadConfiguration() {

        String publisherConfigurationPath = "src/test/resources/publisher-description-configuration.json";
        PublisherConfiguration publisherConfiguration = new PublisherConfiguration(publisherConfigurationPath, new ObjectMapper());

        assertEquals(3, publisherConfiguration.getPublisherDescriptions().size());
    }

    @Test
    public void givenInvalidConfigurationFile_whenInstantiating_thenThrowException() {

        String publisherConfigurationPath = "src/test/resources/invalid.json";
        FailedToLoadConfiguration exception = assertThrows(FailedToLoadConfiguration.class,
                () -> new PublisherConfiguration(publisherConfigurationPath, new ObjectMapper()));

        assertEquals("Failed to load configuration file : src/test/resources/invalid.json", exception.getMessage());
    }

    @Test
    public void givenConfigurationFile_whenInstantiating_thenGetEndpointForPublisherId() {

        String publisherConfigurationPath = "src/test/resources/publisher-description-configuration.json";
        PublisherConfiguration publisherConfiguration = new PublisherConfiguration(publisherConfigurationPath, new ObjectMapper());

        assertEquals("http://localhost:3000/publish", publisherConfiguration.getEndpointFor("bahmni-patient-kid"));
    }

    @Test
    public void givenConfigurationFileAndEndpointNotConfiguredForPublisher_whenInstantiating_thenThrowException() {

        String publisherConfigurationPath = "src/test/resources/publisher-description-configuration.json";
        PublisherConfiguration publisherConfiguration = new PublisherConfiguration(publisherConfigurationPath, new ObjectMapper());

        EndpointNotConfiguredForPublisherException exception = assertThrows(EndpointNotConfiguredForPublisherException.class,
                () -> publisherConfiguration.getEndpointFor("bahmni-patient-kid-123"));

        assertEquals("Endpoint not configured for publisher id : bahmni-patient-kid-123", exception.getMessage());
    }

    @Test
    public void givenConfigurationFile_whenInstantiating_thenGetTopicForPublisherId() {

        String publisherConfigurationPath = "src/test/resources/publisher-description-configuration.json";
        PublisherConfiguration publisherConfiguration = new PublisherConfiguration(publisherConfigurationPath, new ObjectMapper());

        assertEquals("test-topic", publisherConfiguration.getTopicFor("gcp-patient-registration").getName());
    }

    @Test
    public void givenConfigurationFileAndTopicNotConfiguredForPublisher_whenInstantiating_thenThrowException() {

        String publisherConfigurationPath = "src/test/resources/publisher-description-configuration.json";
        PublisherConfiguration publisherConfiguration = new PublisherConfiguration(publisherConfigurationPath, new ObjectMapper());

        TopicNotConfiguredForPublisherException exception = assertThrows(TopicNotConfiguredForPublisherException.class,
                () -> publisherConfiguration.getTopicFor("gcp-patient-registration-123"));

        assertEquals("Topic not configured for publisher id : gcp-patient-registration-123", exception.getMessage());
    }
}