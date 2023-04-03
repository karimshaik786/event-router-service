package org.bahmni.eventrouterservice;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Topic {
    private String name;
    private String subscriptionId;

    public Topic() {
    }

    public String getName() {
        return name;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }
}
