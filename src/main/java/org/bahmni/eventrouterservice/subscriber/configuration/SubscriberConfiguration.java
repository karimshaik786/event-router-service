package org.bahmni.eventrouterservice.subscriber.configuration;

import org.bahmni.eventrouterservice.model.ServiceName;
import org.bahmni.eventrouterservice.model.Topic;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bahmni.eventrouterservice.exception.FailedToLoadConfiguration;
import org.bahmni.eventrouterservice.publisher.configuration.PublisherConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SubscriberConfiguration {

    Logger logger = LoggerFactory.getLogger(PublisherConfiguration.class);
    private List<SubscriberDescription> subscriberDescriptions = new ArrayList<>();
    private Set<SubscriberSchedule> subscriberSchedules = new HashSet<>();

    @Autowired
    public SubscriberConfiguration(@Value("${subscriber-configuration-file}") String subscriberConfigurationFile,
                                   @Value("${subscriber-schedule-configuration-file}") String subscriberScheduleConfigurationFile,
                                   ObjectMapper objectMapper) {
        loadDescription(subscriberConfigurationFile, objectMapper);
        loadSchedule(subscriberScheduleConfigurationFile, objectMapper);
    }

    private void loadDescription(String subscriberConfigurationFile, ObjectMapper objectMapper) {

        try {
            File subscriberConfigFile = new FileSystemResource(subscriberConfigurationFile).getFile();
            this.subscriberDescriptions = objectMapper.readValue(subscriberConfigFile, new TypeReference<>() {});
        } catch (IOException exception) {
            logger.error("Failed to load configuration for file : " + subscriberConfigurationFile);
            throw new FailedToLoadConfiguration(subscriberConfigurationFile, exception);
        }
    }

    private void loadSchedule(String subscriberScheduleConfigurationFile, ObjectMapper objectMapper) {

        try {
            File subscriberScheduleConfigFile = new FileSystemResource(subscriberScheduleConfigurationFile).getFile();
            this.subscriberSchedules = objectMapper.readValue(subscriberScheduleConfigFile, new TypeReference<>() {});
        } catch (IOException exception) {
            logger.error("Failed to load configuration for file : " + subscriberScheduleConfigurationFile);
            throw new FailedToLoadConfiguration(subscriberScheduleConfigurationFile, exception);
        }
    }


    public List<SubscriberDescription> getSubscribersAsPerOrderOfSubscriptionFor(ServiceName service) {
        return subscriberDescriptions.stream()
                .filter(subscriberDesc -> subscriberDesc.isForService(service))
                .sorted(Comparator.comparing(SubscriberDescription::getOrderOfSubscription))
                .collect(Collectors.toList());
    }

    public Set<SubscriberSchedule> subscriberSchedules() {return Collections.unmodifiableSet(subscriberSchedules);}

    public int totalSubscribersToBeScheduled() {
        return subscriberSchedules.size();
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class SubscriberDescription {
        private String publisherId;
        private Source source;

        private int orderOfSubscription = 1;

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

        public Integer getOrderOfSubscription() { return orderOfSubscription; }

        public Integer maxMessages() { return source.topic.getMaxMessages(); }
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Source {
        private ServiceName serviceName;
        private Topic topic;
        private String endpoint;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static class SubscriberSchedule {
        private ServiceName serviceName;
        private String cron;

        public SubscriberSchedule() {}

        public String getCron() {
            return cron;
        }

        public ServiceName getServiceName() {
            return serviceName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SubscriberSchedule that)) return false;

            return serviceName.equals(that.serviceName);
        }

        @Override
        public int hashCode() {
            return serviceName.hashCode();
        }
    }
}
