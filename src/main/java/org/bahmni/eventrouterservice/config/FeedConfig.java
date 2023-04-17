package org.bahmni.eventrouterservice.config;

import lombok.AllArgsConstructor;
import org.bahmni.webclients.ConnectionDetails;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.openmrs.OpenMRSLoginAuthenticator;
import org.ict4h.atomfeed.client.AtomFeedProperties;
import org.ict4h.atomfeed.client.repository.jdbc.AllFailedEventsJdbcImpl;
import org.ict4h.atomfeed.client.repository.jdbc.AllMarkersJdbcImpl;
import org.ict4h.atomfeed.server.transaction.AtomFeedSpringTransactionSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@AllArgsConstructor
@Configuration
public class FeedConfig {
    private final BahmniFeedProperties feedProperties;

    @Bean
    public AtomFeedSpringTransactionSupport atomFeedSpringTransactionSupport(PlatformTransactionManager platformTransactionManager, DataSource dataSource) {
        return new AtomFeedSpringTransactionSupport(platformTransactionManager, dataSource);
    }

    @Bean
    public HttpClient openmrsClient() {
        ConnectionDetails connectionDetails = new ConnectionDetails(feedProperties.getOpenmrs().getAuthUri()
                , feedProperties.getOpenmrs().getUser(), feedProperties.getOpenmrs().getPassword(),
                feedProperties.getOpenmrs().getConnectionTimeoutInMilliseconds(), feedProperties.getOpenmrs().getReplyTimeoutInMilliseconds());
        return new org.bahmni.webclients.HttpClient(connectionDetails,
                new OpenMRSLoginAuthenticator(connectionDetails));
    }

    @Bean
    public AllMarkersJdbcImpl markersJdbc(AtomFeedSpringTransactionSupport atomFeedSpringTransactionSupport) {
        return new AllMarkersJdbcImpl(atomFeedSpringTransactionSupport);
    }

    @Bean
    public AllFailedEventsJdbcImpl failedEventsJdbc(AtomFeedSpringTransactionSupport atomFeedSpringTransactionSupport) {
        return new AllFailedEventsJdbcImpl(atomFeedSpringTransactionSupport);
    }

    @Bean
    public AtomFeedProperties atomFeedProperties() {
        AtomFeedProperties atomFeedProperties = new AtomFeedProperties();
        atomFeedProperties.setConnectTimeout(feedProperties.getFeed().getConnectionTimeoutInMilliseconds());
        atomFeedProperties.setReadTimeout(feedProperties.getFeed().getReplyTimeoutInMilliseconds());
        atomFeedProperties.setMaxFailedEvents(feedProperties.getFeed().getMaxFailedEvents());
        atomFeedProperties.setFailedEventMaxRetry(feedProperties.getFeed().getFailedEventMaxRetry());
        atomFeedProperties.setControlsEventProcessing(true);
        return atomFeedProperties;
    }
//
//    @Bean
//    public TaskScheduler taskScheduler() {
//        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
//
//    }
}
