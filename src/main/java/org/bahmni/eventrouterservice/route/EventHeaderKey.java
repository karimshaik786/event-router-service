package org.bahmni.eventrouterservice.route;

public enum EventHeaderKey {
    EVENT_TYPE("eventType"),
    PAYLOAD_ID("payloadId"),
    EVENT_ID("eventId"),
    PUBLISHED_DATE_TIME("publishedDateTime");

    private final String value;

    EventHeaderKey(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
