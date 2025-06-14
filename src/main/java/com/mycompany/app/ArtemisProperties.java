package com.mycompany.app;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import lombok.*;

@Configuration
@ConfigurationProperties(prefix = "artemis")
@ConfigurationPropertiesScan
@Getter @Setter
public class ArtemisProperties {
    private String url;
    private String username;
    private String password;
    private String queue;
}
