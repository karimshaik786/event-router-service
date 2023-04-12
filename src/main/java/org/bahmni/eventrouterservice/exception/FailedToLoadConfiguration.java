package org.bahmni.eventrouterservice.exception;

import java.io.IOException;

public class FailedToLoadConfiguration extends RuntimeException {
    public FailedToLoadConfiguration(String configurationFile, IOException exception) {
        super("Failed to load configuration file : "+configurationFile, exception);
    }
}
