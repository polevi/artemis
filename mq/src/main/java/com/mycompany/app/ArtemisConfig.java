package com.mycompany.app;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableAsync;

import lombok.*;

@Configuration
@ConfigurationProperties(prefix = "app.artemis")
@ConfigurationPropertiesScan
@EnableAsync
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
    public JmsTransactionManager transactionManager() {
        return new JmsTransactionManager(connectionFactory());
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setTransactionManager(transactionManager());
        factory.setConcurrency("1-1");
        factory.setSessionTransacted(true);
        factory.setMessageConverter(customMessageConverter());
        return factory;
    }
    
    @Bean
    public CustomMessageConverter customMessageConverter() {
        return new CustomMessageConverter();
    }    
}
