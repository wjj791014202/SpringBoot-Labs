package com.apollo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApolloRefreshConfiguration {
    public ApolloRefreshConfiguration() {
    }

    @Bean
    public EnvironmentRefresher environmentRefresher() {
        return new EnvironmentRefresher();
    }
}
