package org.bifu.distributed.auth.conf;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    private static final String BROWSER_USERNAME = "client1";
    private static final String BROWSER_PASSWORD = "secret1";
    private static final String APP_USERNAME = "client2";
    private static final String APP_PASSWORD = "secret2";

    @Bean(name = "browserRestTemplate")
    public RestTemplate pcRestTemplate(RestTemplateBuilder builder) {
        return builder.basicAuthorization(BROWSER_USERNAME, BROWSER_PASSWORD).build();
    }

    @Bean(name = "appRestTemplate")
    public RestTemplate mobileRestTemplate(RestTemplateBuilder builder) {
        return builder.basicAuthorization(APP_USERNAME, APP_PASSWORD).build();
    }
    
}
