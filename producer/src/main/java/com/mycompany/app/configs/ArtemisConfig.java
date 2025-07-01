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

import com.mycompany.app.converters.SwiftMTMessageConverter;

import lombok.*;

@Configuration
@ConfigurationProperties(prefix = "app.artemis")
@ComponentScan
@EnableJms
@Getter @Setter
public class ArtemisConfig {
    private String url;
    private String username;
    private String password;
    private String queue;
    private int batchSize;

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
        jmsTemplate.setMessageConverter(new SwiftMTMessageConverter());
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }
}
