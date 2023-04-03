package org.bahmni.eventrouterservice.subscriber.configuration;

import org.bahmni.eventrouterservice.ServiceName;
import org.bahmni.eventrouterservice.Topic;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SubscriberConfig {

    List<SubscriberDescription> subscriberDescriptions = new ArrayList<>();
    private final String subscriberConfigFile;
    private final ObjectMapper objectMapper;

    public SubscriberConfig(@Value("${subscriber-config-file}") String subscriberConfigFile, ObjectMapper objectMapper) {
        this.subscriberConfigFile = subscriberConfigFile;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    private void loadConfig() {
        try {
            File configFile = new FileSystemResource(subscriberConfigFile).getFile();
            this.subscriberDescriptions = objectMapper.readValue(configFile, new TypeReference<List<SubscriberDescription>>(){});
        } catch (IOException e) {
            throw new RuntimeException("Error in loading config "+e.getMessage());
        }
    }

    public List<SubscriberDescription> getSubscriberFor(ServiceName service) {
        return subscriberDescriptions.stream().filter(subscriberDesc -> subscriberDesc.isForService(service)).collect(Collectors.toList());
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class SubscriberDescription {
        private String publisherId;
        private Source source;

        public SubscriberDescription() {}

        public boolean isForService(ServiceName service) {
            return source.serviceName == service;
        }

        public String getEndpoint() {
            return source.endpoint;
        }

        public String getPublisherId() {
            return publisherId;
        }

        public String getSubscriptionId() {
            return source.topic.getSubscriptionId();
        }
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Source {
        private ServiceName serviceName;
        private Topic topic;
        private String endpoint;
    }
}
