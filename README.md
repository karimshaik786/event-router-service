# event-router-service

Event router service will be responsible for routing the events from Bhamni to external system and vice versa.

As of now it supports routing the message from Bahmni to GCP Pub sub and GCP pub sub to Bahmni. 
We can incrementally add the support for external systems.

Configuration has to be provided both environment variable and through external files.

Run test using below command:
mvn clean install

Run Application using below command:
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-DGCP_PUB_SUB_ENABLED=true -DGCP_PROJECT_ID=<PROJECT_ID> -DGCP_CREDENTIALS_FILE_LOCATION=<GCP_PUB_SUB_AUTHENTICATION_FILE>.json -DPUBLISHER_CONFIGURATION_FILE_LOCATION=<PUBLISHER_CONFIGURATION_DESCRIPTION_FILE>.json -DSUBSCRIBER_CONFIGURATION_FILE_LOCATION=<SUBSCRIBER_CONFIGURATION_FILE_LOCATION>.json -DSUBSCRIBER_SCHEDULE_CONFIGURATION_FILE_LOCATION=<SUBSCRIBER_SCHEDULE_CONFIGURATION_FILE_LOCATION>.json"

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
            "endpoint": "https://gorest.co.in/public/v2/users"
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