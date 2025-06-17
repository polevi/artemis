package com.mycompany.app;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import lombok.*;

@Configuration
@ConfigurationProperties(prefix = "controller")
@ConfigurationPropertiesScan
@Getter @Setter
public class ControllerProperties {
}
