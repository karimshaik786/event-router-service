package org.bahmni.eventrouterservice.atomfeed.client;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.bahmni.eventrouterservice.config.BahmniFeedProperties;
import org.bahmni.webclients.ClientCookies;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.AtomFeedProperties;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.jdbc.AllFailedEventsJdbcImpl;
import org.ict4h.atomfeed.client.repository.jdbc.AllMarkersJdbcImpl;
import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.ict4h.atomfeed.client.service.FeedClient;
import org.ict4h.atomfeed.server.transaction.AtomFeedSpringTransactionSupport;
import org.springframework.stereotype.Component;

import java.net.URI;

@AllArgsConstructor
@Component
public class AtomFeedClientFactory {
    private final AtomFeedSpringTransactionSupport atomFeedSpringTransactionSupport;
    private final AllMarkersJdbcImpl allMarkersJdbc;
    private final AllFailedEventsJdbcImpl failedEventsJdbc;
    private final AtomFeedProperties atomFeedProperties;
    private final HttpClient bahmniHttpClient;
    private final BahmniFeedProperties feedProperties;

    public FeedClient get(String feedName,
                          EventWorker eventWorker) {
        ClientCookies cookies = getCookies(bahmniHttpClient, feedProperties.getOpenmrs().getAuthUri());
        AllFeeds allFeeds = new AllFeeds(atomFeedProperties, cookies);
        return new AtomFeedClient(allFeeds, allMarkersJdbc, failedEventsJdbc,
                atomFeedProperties, atomFeedSpringTransactionSupport, getFeedUri(feedName), eventWorker);
    }

    @SneakyThrows
    private URI getFeedUri(String feedName) {
        return new URI(feedName);
    }

    private ClientCookies getCookies(HttpClient authenticatedWebClient, String urlString) {
        return authenticatedWebClient.getCookies(getFeedUri(urlString));
    }
}
