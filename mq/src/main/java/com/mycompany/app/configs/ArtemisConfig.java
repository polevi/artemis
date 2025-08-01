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

import com.mycompany.app.consumer.BatchJmsListenerContainerFactory;
import com.mycompany.app.converters.SwiftMTMessageConverter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Configuration
@ConfigurationProperties(prefix = "app.artemis")
@ComponentScan
@EnableJms
@Getter @Setter
@Validated
@Slf4j
public class ArtemisConfig {
    @NotNull String url;
    @NotNull String username;
    @NonNull String password;
    @NotNull String queue;
    @Positive int batchSize;

    @Bean
    ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        connectionFactory.setUser(username);
        connectionFactory.setPassword(password);
        connectionFactory.setTransactionBatchSize(batchSize);
        return connectionFactory;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        return new CachingConnectionFactory(connectionFactory());
    }

    @Bean
    public JmsTransactionManager transactionManager() {
        return new JmsTransactionManager(cachingConnectionFactory());
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory());
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }

    @Bean
    public BatchJmsListenerContainerFactory batchJmsListenerContainerFactory() {
        BatchJmsListenerContainerFactory factory = new BatchJmsListenerContainerFactory(batchSize);
        factory.setConnectionFactory(cachingConnectionFactory());
        factory.setTransactionManager(transactionManager());
        factory.setConcurrency("1-1");
        factory.setSessionTransacted(true);
        factory.setMessageConverter(new SwiftMTMessageConverter());
        factory.setAutoStartup(true);
        factory.setErrorHandler(jmsErrorHandler());
        return factory;        
    }        

    @Bean
    public ErrorHandler jmsErrorHandler() {
        return e -> {
            log.error("JMS Listener encountered an error: {}", e.getMessage(), e);
        };
    }
}
