package com.mycompany.app.configs;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.ErrorHandler;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.app.consumer.BatchJmsListenerContainerFactory;
import com.mycompany.app.converters.SwiftMTMessageConverter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Configuration
@ConfigurationProperties(prefix = "app.artemis.lan")
@ComponentScan
@EnableJms
@Getter @Setter
@Validated
@Slf4j
public class ArtemisConfigLan {
    @NotNull String url;
    @NotNull String username;
    @NonNull String password;
    @NotNull String queue;
    String selector;
    @Positive int batchSize;

    @Bean
    ActiveMQConnectionFactory lanConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        connectionFactory.setUser(username);
        connectionFactory.setPassword(password);
        connectionFactory.setTransactionBatchSize(batchSize);
        return connectionFactory;
    }

    @Bean
    public CachingConnectionFactory lanCachingConnectionFactory() {
        return new CachingConnectionFactory(lanConnectionFactory());
    }

    //@Bean
    public JmsTransactionManager lanTransactionManager() {
        return new JmsTransactionManager(lanCachingConnectionFactory());
    }

    @Bean
    public JmsTemplate lanJmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(lanCachingConnectionFactory());
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }

    @Bean
    public BatchJmsListenerContainerFactory lanBatchJmsListenerContainerFactory(ObjectMapper mapper) {
        BatchJmsListenerContainerFactory factory = new BatchJmsListenerContainerFactory(batchSize);
        factory.setConnectionFactory(lanCachingConnectionFactory());
        factory.setTransactionManager(lanTransactionManager());
        factory.setConcurrency("2-2");
        factory.setSessionTransacted(true);
        factory.setMessageConverter(new SwiftMTMessageConverter(mapper));
        factory.setAutoStartup(true);
        factory.setErrorHandler(lanJmsErrorHandler());
        return factory;        
    }        

    @Bean
    public ErrorHandler lanJmsErrorHandler() {
        return e -> {
            log.error("JMS Listener encountered an error: {}", e.getMessage(), e);
        };
    }
}
