package org.bahmni.eventrouterservice.subscriber.bahmni;

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

@Configuration
public class AtomFeedClientConfiguration {

    @Bean
    public AtomFeedSpringTransactionSupport atomFeedSpringTransactionSupport(PlatformTransactionManager platformTransactionManager, DataSource dataSource) {
        return new AtomFeedSpringTransactionSupport(platformTransactionManager, dataSource);
    }

    @Bean
    public HttpClient bahmniHttpClient(AtomFeedClientProperties feedProperties) {
        ConnectionDetails connectionDetails = new ConnectionDetails(feedProperties.getAuthUri()
                , feedProperties.getUser(), feedProperties.getPassword(),
                feedProperties.getConnectionTimeoutInMilliseconds(), feedProperties.getReplyTimeoutInMilliseconds());
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
    public AtomFeedProperties atomFeedProperties(AtomFeedClientProperties feedProperties) {
        AtomFeedProperties atomFeedProperties = new AtomFeedProperties();
        atomFeedProperties.setMaxFailedEvents(feedProperties.getMaxFailedEvents());
        atomFeedProperties.setControlsEventProcessing(true);
        return atomFeedProperties;
    }
}