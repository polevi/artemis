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
@ConfigurationProperties(prefix = "app.artemis.prcs")
@ComponentScan
@EnableJms
@Getter @Setter
@Validated
@Slf4j
public class ArtemisConfigPrcs {
    @NotNull String url;
    @NotNull String username;
    @NonNull String password;
    @NotNull String queue;
    @Positive int batchSize;

    @Bean
    ActiveMQConnectionFactory prcsConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        connectionFactory.setUser(username);
        connectionFactory.setPassword(password);
        connectionFactory.setTransactionBatchSize(batchSize);
        return connectionFactory;
    }

    @Bean
    public CachingConnectionFactory prcsCachingConnectionFactory() {
        return new CachingConnectionFactory(prcsConnectionFactory());
    }

    //@Bean
    public JmsTransactionManager prcsTransactionManager() {
        return new JmsTransactionManager(prcsCachingConnectionFactory());
    }

    @Bean
    public JmsTemplate prcsJmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(prcsCachingConnectionFactory());
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }

    @Bean
    public BatchJmsListenerContainerFactory prcsBatchJmsListenerContainerFactory() {
        BatchJmsListenerContainerFactory factory = new BatchJmsListenerContainerFactory(batchSize);
        factory.setConnectionFactory(prcsCachingConnectionFactory());
        factory.setTransactionManager(prcsTransactionManager());
        factory.setConcurrency("1-1");
        factory.setSessionTransacted(true);
        factory.setMessageConverter(new SwiftMTMessageConverter());
        factory.setAutoStartup(true);
        factory.setErrorHandler(prcsJmsErrorHandler());
        return factory;        
    }        

    @Bean
    public ErrorHandler prcsJmsErrorHandler() {
        return e -> {
            log.error("JMS Listener encountered an error: {}", e.getMessage(), e);
        };
    }
}
