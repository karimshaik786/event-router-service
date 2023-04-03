package org.bahmni.eventrouterservice.subscriber.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class SubscriberThreadPoolConfiguration {

    @Bean
    public ThreadPoolTaskScheduler subscribersThreadPoolConfiguration(SubscriberConfiguration subscriberConfiguration) {

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(subscriberConfiguration.totalSubscribersToBeScheduled());
        taskScheduler.setThreadNamePrefix("subscribers-");
        taskScheduler.initialize();

        return taskScheduler;
    }
}
