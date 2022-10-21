package com.meshgroup.task.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

@TestConfiguration
public class HttpConfiguration {

    @Bean
    public HttpFirewall httpFirewall() {
        return new DefaultHttpFirewall();
    }

}
