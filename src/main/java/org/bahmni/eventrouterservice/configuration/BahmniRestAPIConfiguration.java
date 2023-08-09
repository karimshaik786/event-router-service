package org.bahmni.eventrouterservice.configuration;

import org.bahmni.webclients.ConnectionDetails;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.openmrs.OpenMRSLoginAuthenticator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConditionalOnExpression("${bahmni.activemqToGCP.failed-route-enabled:true} || ${bahmni.activemqToGCP.route-enabled:true}")
@Profile("!test")
public class BahmniRestAPIConfiguration {

    @Bean
    public HttpClient bahmniHttpClient(BahmniAPIProperties bahmniAPIProperties) {
        ConnectionDetails connectionDetails = new ConnectionDetails(bahmniAPIProperties.getAuthUrl()
                , bahmniAPIProperties.getUser(), bahmniAPIProperties.getPassword(),
                bahmniAPIProperties.getConnectionTimeoutInMilliseconds(), bahmniAPIProperties.getReplyTimeoutInMilliseconds());
        return new org.bahmni.webclients.HttpClient(connectionDetails,
                new OpenMRSLoginAuthenticator(connectionDetails));
    }
}