package com.apollo;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApolloSecurityConfig {
    public ApolloSecurityConfig() {
    }

    @Bean
    public JasyptEncrypt jasyptEncrypt() {
        return new JasyptEncrypt();
    }
}
