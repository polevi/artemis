package com.mycompany.app.producer;

import jakarta.jms.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.mycompany.app.ServiceRunner;
import com.mycompany.app.ShutdownHook;
import com.mycompany.app.config.ArtemisConfig;
import com.mycompany.app.messages.SwiftMTMessage;
import com.mycompany.app.swiftmt.SwiftMTHelper;

@Service("producer")
@Slf4j
public class Producer implements ServiceRunner {
    @Autowired
    ShutdownHook shutdownHook;

    @Autowired
    ArtemisConfig artemisProperties;

    @Autowired
    SwiftMTHelper swiftMTHelper;

    @Autowired 
    ActiveMQConnectionFactory artemisConnectionFactory;       

    @Override
    @Async("threadPoolTaskExecutor")
    @Retryable(value = RuntimeException.class, maxAttempts = Integer.MAX_VALUE, backoff = @Backoff(delay = 5000), listeners = {"retryListener"})
    public void run() throws Exception {
        log.info("Producer started");
        
        try (JMSContext context = artemisConnectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
            context.start();
    
            Queue queue = context.createQueue(artemisProperties.getQueue());

            JMSProducer producer = context.createProducer();
            //producer.setTimeToLive(30000);
            producer.setDeliveryDelay(5000);

            log.info("Start producing messages to address: {}", artemisProperties.getQueue());

            int n = 0;
            while(!shutdownHook.isTerminating()) {
                SwiftMTMessage msg = new SwiftMTMessage(n, LocalDate.now(), swiftMTHelper.createMT103(n));
                ObjectMessage message = context.createObjectMessage(msg);
                producer.send(queue, message);

                if (n % artemisProperties.getBatchSize() == 0) {
                    log.info("Batch of {} messages has been sent successfully.", artemisProperties.getBatchSize());  
                }

                n++;
            }
        }
    }  
}
