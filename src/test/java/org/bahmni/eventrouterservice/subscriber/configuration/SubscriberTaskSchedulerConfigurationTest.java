package org.bahmni.eventrouterservice.subscriber.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bahmni.eventrouterservice.subscriber.bahmni.BahmniEventSubscriber;
import org.bahmni.eventrouterservice.subscriber.configuration.SubscriberConfiguration.SubscriberSchedule;
import org.bahmni.eventrouterservice.subscriber.gcp.GCPEventSubscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SubscriberTaskSchedulerConfigurationTest {

    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private SubscriberConfiguration subscriberConfiguration;
    @InjectMocks
    private SubscriberTaskSchedulerConfiguration subscriberTaskSchedulerConfiguration;

    @Test
    public void givenSubscriberSchedule_whenConfiguringTask_thenSchedulerShouldFetchSchedule() {
        ScheduledTaskRegistrar scheduledTaskRegistrar = new ScheduledTaskRegistrar();

        Mockito.when(subscriberConfiguration.subscriberSchedules()).thenReturn(emptySchedule());

        subscriberTaskSchedulerConfiguration.configureTasks(scheduledTaskRegistrar);

        Mockito.verify(subscriberConfiguration).subscriberSchedules();
    }

    @Test
    public void givenSubscriberSchedule_whenConfiguringTask_thenSchedulerShouldCreateCronTaskBasedOnSchedule() {
        ScheduledTaskRegistrar scheduledTaskRegistrar = new ScheduledTaskRegistrar();

        Mockito.when(subscriberConfiguration.subscriberSchedules()).thenReturn(defaultSchedule());
        Mockito.when(applicationContext.getBean(BahmniEventSubscriber.class)).thenReturn(Mockito.mock(BahmniEventSubscriber.class));
        Mockito.when(applicationContext.getBean(GCPEventSubscriber.class)).thenReturn(Mockito.mock(GCPEventSubscriber.class));

        subscriberTaskSchedulerConfiguration.configureTasks(scheduledTaskRegistrar);

        assertEquals(2, scheduledTaskRegistrar.getCronTaskList().size());
    }

    @Test
    public void givenSubscriberSchedule_whenConfiguringTask_thenSchedulerShouldCreateCronTaskBasedForBahmni() {
        ScheduledTaskRegistrar scheduledTaskRegistrar = new ScheduledTaskRegistrar();

        Mockito.when(subscriberConfiguration.subscriberSchedules()).thenReturn(defaultSchedule());
        BahmniEventSubscriber mockBahmniSubscriber = Mockito.mock(BahmniEventSubscriber.class);
        Mockito.when(applicationContext.getBean(BahmniEventSubscriber.class)).thenReturn(mockBahmniSubscriber);
        GCPEventSubscriber mockGCPSubscriber = Mockito.mock(GCPEventSubscriber.class);
        Mockito.when(applicationContext.getBean(GCPEventSubscriber.class)).thenReturn(mockGCPSubscriber);

        subscriberTaskSchedulerConfiguration.configureTasks(scheduledTaskRegistrar);

        assertEquals("30 13 10 * * *", scheduledTaskRegistrar.getCronTaskList().get(0).getExpression());
        assertEquals(mockBahmniSubscriber, scheduledTaskRegistrar.getCronTaskList().get(0).getRunnable());
    }

    @Test
    public void givenSubscriberSchedule_whenConfiguringTask_thenSchedulerShouldCreateCronTaskBasedForGCP() {
        ScheduledTaskRegistrar scheduledTaskRegistrar = new ScheduledTaskRegistrar();

        Mockito.when(subscriberConfiguration.subscriberSchedules()).thenReturn(defaultSchedule());
        BahmniEventSubscriber mockBahmniSubscriber = Mockito.mock(BahmniEventSubscriber.class);
        Mockito.when(applicationContext.getBean(BahmniEventSubscriber.class)).thenReturn(mockBahmniSubscriber);
        GCPEventSubscriber mockGCPSubscriber = Mockito.mock(GCPEventSubscriber.class);
        Mockito.when(applicationContext.getBean(GCPEventSubscriber.class)).thenReturn(mockGCPSubscriber);

        subscriberTaskSchedulerConfiguration.configureTasks(scheduledTaskRegistrar);

        assertEquals("30 19 15 * * *", scheduledTaskRegistrar.getCronTaskList().get(1).getExpression());
        assertEquals(mockGCPSubscriber, scheduledTaskRegistrar.getCronTaskList().get(1).getRunnable());
    }

    private Set<SubscriberSchedule> defaultSchedule() {
        String schedule = """
                [
                  {
                    "serviceName": "BAHMNI",
                    "cron": "30 13 10 * * *"
                  },
                  {
                    "serviceName": "GCP",
                    "cron": "30 19 15 * * *"
                  }
                ]
                """;
        try {
            Set<SubscriberSchedule> subscriberSchedules = new ObjectMapper().readValue(schedule, new TypeReference<>() {});
            return subscriberSchedules
                    .stream()
                    .sorted(Comparator.comparing(SubscriberSchedule::getServiceName))
                    .collect(Collectors.toCollection( LinkedHashSet::new ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Set<SubscriberSchedule> emptySchedule() {
        String schedule = """
                []
                """;
        try {
            return new ObjectMapper().readValue(schedule, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}