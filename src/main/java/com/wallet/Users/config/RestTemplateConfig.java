package com.wallet.Users.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@Configuration
@PropertySource("classpath:application.properties")
public class RestTemplateConfig {

    @Bean
    public RestTemplate resttemplate() {
        return new RestTemplate();
    }

}
