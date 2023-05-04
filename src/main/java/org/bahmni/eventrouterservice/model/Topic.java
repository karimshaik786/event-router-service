package org.bahmni.eventrouterservice.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Topic {
    private String name;
    private String subscriptionId;
    private int maxMessages;

    public Topic(String name) {
        this.name = name;
    }

    public Topic() {}
    public int getMaxMessages() { return maxMessages; }

    public String getName() { return name; }
    public String getSubscriptionId() { return subscriptionId; }
}
