## event-router-service

Event router service routes the events from Bahmni to external system and vice versa.
As of now it supports routing the message from Bahmni to GCP Pub sub.
Configuration has to be provided both as an environment variable and through configuration files.
Note: We expect the message to be received and published in JSON format.

## Packaging
```mvn clean package```

## Prerequisite
    JDK 1.17

## Run Application using below command:

mvn spring-boot:run -Dspring-boot.run.jvmArguments="-DGCP_PUB_SUB_ENABLED=true -DGCP_PROJECT_ID=<GCP_PROJECT_ID> -DGCP_CREDENTIALS_FILE_LOCATION=<GCP_PUB_SUB_AUTHENTICATION_FILE>.json -DBAHMNI_ACTIVEMQ_BROKER_URL=<BAHMNI_ACTIVEMQ_BROKER_URL> -DBAHMNI_ACTIVEMQ_TO_GCP_ROUTE_ENABLED=true -DBAHMNI_ACTIVEMQ_TO_GCP_FAILED_ROUTE_ENABLED=false -DROUTE_DESCRIPTION_FILE_LOCATION=<ROUTE_DESCRIPTION_FILE_LOCATION>  -DLOGGING_LEVEL=INFO -DOPENMRS_HOST=<OPENMRS_HOST> -DOPENMRS_PORT=<OPENMRS_PORT> -DOPENMRS_USER=<OPENMRS_USER> -DOPENMRS_PASSWORD=<OPENMRS_PASSWORD>"

example:
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-DGCP_PUB_SUB_ENABLED=true -DGCP_PROJECT_ID=gcp-pubsub-id -DGCP_CREDENTIALS_FILE_LOCATION=/Users/Documents/gcp-pubsub-93eb.json -DBAHMNI_ACTIVEMQ_BROKER_URL=tcp://localhost:61616 -DBAHMNI_ACTIVEMQ_TO_GCP_ROUTE_ENABLED=true -DBAHMNI_ACTIVEMQ_TO_GCP_FAILED_ROUTE_ENABLED=false -DROUTE_DESCRIPTION_FILE_LOCATION=/Users/riteshghiya/Documents/route-descriptions.json -DLOGGING_LEVEL=INFO -DOPENMRS_HOST=localhost -DOPENMRS_PORT=8080 -DOPENMRS_USER=user -DOPENMRS_PASSWORD=pass"

## Sample config file:

sample configuration file: [](src/test/resources/route-descriptions.json)

Explanation:
- source define the bahmni topic to be consumed.
- destination defines the gcp topic to be published to based on the eventType. This eventType we expect to receive in header.
- errorDestination defines the one of the queue in JMS to be published to in case of error.
- maxRetryDelivery defines the number of times the message should be retried to be published to gcp topic in case of error.
- retryDelay defines the delay between each retry.
- cronExpressionForRetryStart defines the cron expression to start the scheduler to process the failed events.
- cronExpressionForRetryStop defines the cron expression to stop the scheduler to process the failed events.
- additionalProperties defines the additional properties to be added to the event payload.
- derivedProperties defines the list of properties to be created from the event payload which can used for either filtering or adding new properties or fetching patient data etc.
  - Example : ""patientUuid": "$.patient.uuid"": "$.patient.uuid". Here patientUuid is the key and the value defines the json path based on event payload from where we have to fetch value.
- filterBy defines the properties to be filtered out from either the event payload or From patient payload.
  - eventProperties defines the list of properties to be considered for filtering from event payload.
  - patientProperties defines the list of properties to be considered for filtering from patient payload which are getting fetched from Bahmni based on patient uuid.
- healthCheckDestination is the destination which we can hit to ensure connectivity with destination. Right now using it before start processing failed messages only.