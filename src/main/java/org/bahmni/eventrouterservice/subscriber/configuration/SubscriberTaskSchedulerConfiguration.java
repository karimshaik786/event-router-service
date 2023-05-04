package org.bahmni.eventrouterservice.subscriber.configuration;

import org.bahmni.eventrouterservice.model.ServiceName;
import org.bahmni.eventrouterservice.subscriber.bahmni.BahmniEventSubscriber;
import org.bahmni.eventrouterservice.subscriber.gcp.GCPEventSubscriber;
import org.bahmni.eventrouterservice.subscriber.configuration.SubscriberConfiguration.SubscriberSchedule;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SubscriberTaskSchedulerConfiguration implements SchedulingConfigurer {

    private final ApplicationContext applicationContext;
    private final SubscriberConfiguration subscriberConfiguration;

    public SubscriberTaskSchedulerConfiguration(ApplicationContext applicationContext, SubscriberConfiguration subscriberConfiguration) {
        this.applicationContext = applicationContext;
        this.subscriberConfiguration = subscriberConfiguration;
    }

    @Override
    public void configureTasks(@NonNull ScheduledTaskRegistrar taskRegistrar) {

        Set<SubscriberSchedule> subscriberSchedules = subscriberConfiguration.subscriberSchedules();
        subscriberSchedules
            .forEach(schedule -> {
                CronTrigger cronTrigger = new CronTrigger(schedule.getCron());
                Runnable subscriber = getSubscriberForService(schedule.getServiceName());
                taskRegistrar.addCronTask(new CronTask(subscriber, cronTrigger));
            });

    }

    private Runnable getSubscriberForService(ServiceName serviceName) {
        return switch (serviceName) {
            case BAHMNI -> applicationContext.getBean(BahmniEventSubscriber.class);
            case GCP -> applicationContext.getBean(GCPEventSubscriber.class);
        };
    }
}
