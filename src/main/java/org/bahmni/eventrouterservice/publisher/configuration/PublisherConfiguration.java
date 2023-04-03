package org.bahmni.eventrouterservice.publisher.configuration;

import org.bahmni.eventrouterservice.ServiceName;
import org.bahmni.eventrouterservice.Topic;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class PublisherConfiguration {

    private List<PublisherDescription> publisherDescriptions;
    private final String publisherConfigFile;
    private final ObjectMapper objectMapper;

    public PublisherConfiguration(@Value("${publisher-config-file}") String publisherConfigFile, ObjectMapper objectMapper) {
        this.publisherConfigFile = publisherConfigFile;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    private void loadConfig() {
        try {
            File configFile = new FileSystemResource(publisherConfigFile).getFile();
            this.publisherDescriptions = objectMapper.readValue(configFile, new TypeReference<List<PublisherDescription>>() {});

        } catch (IOException e) {
            throw new RuntimeException("Error in loading config "+e.getMessage());
        }
    }

    public List<PublisherDescription> getPublisherDescriptions() {
        return this.publisherDescriptions;
    }

    public Optional<String> getEndpointFor(String publisherId) {
        return this.publisherDescriptions.stream()
                .filter(publisherDescription -> publisherDescription.getId().equals(publisherId))
                .findFirst()
                .map(PublisherDescription::getEndpoint);
    }

    public Optional<Topic> getTopicFor(String publisherId) {
        return this.publisherDescriptions.stream()
                .filter(publisherDescription -> publisherDescription.getId().equals(publisherId))
                .findFirst()
                .map(PublisherDescription::getTopic);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class PublisherDescription {
        private String id;
        private Destination destination;

        public PublisherDescription() {}

        public String getId() {
            return id;
        }

        public ServiceName getServiceNameToPublish() {
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
    public static class Destination {
        private ServiceName serviceName;
        private String endpoint;
        private Topic topic;

        public Destination() {
        }
    }
}
