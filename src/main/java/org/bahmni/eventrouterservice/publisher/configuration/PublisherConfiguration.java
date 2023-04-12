package org.bahmni.eventrouterservice.publisher.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bahmni.eventrouterservice.configuration.ServiceName;
import org.bahmni.eventrouterservice.configuration.Topic;
import org.bahmni.eventrouterservice.exception.FailedToLoadConfiguration;
import org.bahmni.eventrouterservice.publisher.common.exception.EndpointNotConfiguredForPublisherException;
import org.bahmni.eventrouterservice.publisher.common.exception.TopicNotConfiguredForPublisherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class PublisherConfiguration {

    Logger logger = LoggerFactory.getLogger(PublisherConfiguration.class);

    private List<PublisherDescription> publisherDescriptions;

    public PublisherConfiguration(@Value("${publisher-configuration-file}") String publisherConfigFile, ObjectMapper objectMapper) {
        loadDescription(publisherConfigFile, objectMapper);
    }

    private void loadDescription(String publisherConfigurationFile, ObjectMapper objectMapper) {
        try {
            File configFile = new FileSystemResource(publisherConfigurationFile).getFile();
            this.publisherDescriptions = objectMapper.readValue(configFile, new TypeReference<>() {});

        } catch (IOException exception) {
            logger.error("Failed to load configuration for file : " + publisherConfigurationFile);
            throw new FailedToLoadConfiguration(publisherConfigurationFile, exception);
        }
    }

    public List<PublisherDescription> getPublisherDescriptions() {
        return Collections.unmodifiableList(this.publisherDescriptions);
    }

    public String getEndpointFor(String publisherId) {
        return this.publisherDescriptions.stream()
                .filter(publisherDescription -> publisherDescription.getId().equals(publisherId))
                .findFirst()
                .map(PublisherDescription::getEndpoint)
                .orElseThrow(() -> new EndpointNotConfiguredForPublisherException(publisherId));
    }

    public Topic getTopicFor(String publisherId) {
        return this.publisherDescriptions.stream()
                .filter(publisherDescription -> publisherDescription.getId().equals(publisherId))
                .findFirst()
                .map(PublisherDescription::getTopic)
                .orElseThrow(() -> new TopicNotConfiguredForPublisherException(publisherId));
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class PublisherDescription {
        private String id;
        private Destination destination;

        public PublisherDescription() {}

        public String getId() {
            return id;
        }

        public ServiceName getServiceName() {
            return destination.serviceName;
        }

        public String getEndpoint() {
            return destination.endpoint;
        }

        public Topic getTopic() {
            return destination.topic;
        }
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    // TODO: TopicBasedDestination and URLBasedDestination
    static class Destination {
        private ServiceName serviceName;
        private String endpoint;
        private Topic topic;

        public Destination() {
        }
    }
}
