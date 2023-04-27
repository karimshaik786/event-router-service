package org.bahmni.eventrouterservice.subscriber.bahmni;

import lombok.AllArgsConstructor;
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
    private final AtomFeedClientProperties feedProperties;

    public FeedClient get(String feedURI,
                          EventWorker eventWorker) {
        ClientCookies cookies = bahmniHttpClient.getCookies(URI.create(feedProperties.getAuthUri()));
        AllFeeds allFeeds = new AllFeeds(atomFeedProperties, cookies);
        return new AtomFeedClient(allFeeds, allMarkersJdbc, failedEventsJdbc,
                atomFeedProperties, atomFeedSpringTransactionSupport, URI.create(feedURI), eventWorker);
    }
}
