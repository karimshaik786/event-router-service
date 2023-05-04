# event-router-service

Event router service routes the events from Bhamni to external system and vice versa. 
As of now it supports routing the message from Bahmni to GCP Pub sub and GCP pub sub to Bahmni.
Configuration has to be provided both as an environment variable and through configuration files.

Subscription from bahmni

Run test using below command:
mvn clean install

Run Application using below command:
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-DGCP_PUB_SUB_ENABLED=true -DGCP_PROJECT_ID=<PROJECT_ID> -DGCP_CREDENTIALS_FILE_LOCATION=<GCP_PUB_SUB_AUTHENTICATION_FILE>.json -DPUBLISHER_CONFIGURATION_FILE_LOCATION=<PUBLISHER_CONFIGURATION_DESCRIPTION_FILE>.json -DSUBSCRIBER_CONFIGURATION_FILE_LOCATION=<SUBSCRIBER_CONFIGURATION_FILE_LOCATION>.json -DSUBSCRIBER_SCHEDULE_CONFIGURATION_FILE_LOCATION=<SUBSCRIBER_SCHEDULE_CONFIGURATION_FILE_LOCATION>.json -DEVENT_ROUTER_DB_HOST=http://localhost  -DEVENT_ROUTER_DB_PORT=3306 -DEVENT_ROUTER_DB_USERNAME=event_router_user -DEVENT_ROUTER_DB_PASSWORD=passw0rd -DEVENT_ROUTER_DB_NAME=event_router -DOPENMRS_HOST=localhost -DOPENMRS_PORT=8080 -DOPENMRS_ATOMFEED_USER=admin -DOPENMRS_ATOMFEED_PASSWORD=Admin123 -DMAX_FAILED_EVENTS=1 -DCONNECTION_TIMEOUT_IN_MILLISECONDS=5000 -DREPLY_TIMEOUT_IN_MILLISECONDS=5000 -DLOGGING_LEVEL=INFO"

sample publisher-description-configuration:

[
    {
        "id": "bahmni-patient-kid",
        "destination": {
        "serviceName": "BAHMNI",
        "endpoint": "http://localhost:3000/publish"
        }
    },
    {
        "id": "gcp-patient-registration",
        "destination": {
            "serviceName": "GCP",
            "topic": {
                "name": "test-topic"
            }
        }
    }
]


sample subscriber-description-configuration.json:

[
    {
        "source": {
            "serviceName": "GCP",
            "topic": {
                "subscriptionId": "test-topic-tw-sub",
                "maxMessages": 20
            }
        },
        "publisherId": "bahmni-patient-kid"
    },
    {
        "source": {
            "serviceName": "BAHMNI",
            "endpoint": "/openmrs/ws/atomfeed/patient/recent"
        },
        "orderOfSubscription": 1,
        "publisherId": "gcp-patient-registration"
    }
]

sample subscriber-schedule-configuration.json:

[
    {
        "serviceName": "BAHMNI",
        "cron": "30 37 10 * * *"
    },
    {
        "serviceName": "GCP",
        "cron": "30 40 10 * * *"
    }
]