package com.mycompany.app.consumer;

import java.util.ArrayList;
import java.util.List;

import jakarta.jms.Session;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mycompany.app.dao.AggregatorRepository;
import com.mycompany.app.helpers.SwiftMTHelper;
import com.mycompany.app.messages.SwiftMTMessage;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AggregatorService {

    AggregatorRepository aggregatorRepository;
    JmsTemplate prcsJmsTemplate;

    public AggregatorService(AggregatorRepository aggregatorRepository, @Qualifier("prcsJmsTemplate") JmsTemplate prcsJmsTemplate) {
        this.aggregatorRepository = aggregatorRepository;
        this.prcsJmsTemplate = prcsJmsTemplate;
    }

    @JmsListener(destination = "#{@artemisConfigLan.queue}", containerFactory = "lanBatchJmsListenerContainerFactory")
    @Retryable(maxAttempts = 1, backoff = @Backoff(delay = 1000))
    @Transactional
    public void processSwiftMT(BatchMessage batch, Session session) {
        List<SwiftMTMessage> messages = batch.getMessages(SwiftMTMessage.class);

        int[] batchResult = aggregatorRepository.insertBatch(messages);
        List<SwiftMTMessage> ignoredMessages = processIgnored(messages, batchResult);
        log.info("Inserted {}, skipped {} records", messages.size(), ignoredMessages.size());

        if (messages.size() > 0) {
            Long edno = aggregatorRepository.getNextEdNo();
            log.info("Generated edno: {}", edno);
            prcsJmsTemplate.send("ED", messageCreator -> {
                return messageCreator.createTextMessage(SwiftMTHelper.createED503(messages, edno));
            });
            log.info("Sent ED503 message", messages.size());
        }
    }

    List<SwiftMTMessage> processIgnored(List<SwiftMTMessage> messages, int[] batchResult) {
        List<SwiftMTMessage> ignored = new ArrayList<SwiftMTMessage>();
        for(int i = batchResult.length - 1; i >= 0; i--) {
            if(batchResult[i] == 0) {
                ignored.add(messages.get(i));
                messages.remove(i);
            }
        }
        return ignored;
    }
}
