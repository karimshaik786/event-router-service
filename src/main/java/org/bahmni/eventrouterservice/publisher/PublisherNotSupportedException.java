package org.bahmni.eventrouterservice.publisher;

import org.bahmni.eventrouterservice.ServiceName;

public class PublisherNotSupportedException extends RuntimeException {
    public PublisherNotSupportedException(ServiceName serviceName) {
        super("Service name : "+serviceName+" not supported");
    }
}
