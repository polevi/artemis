package com.mycompany.app.consumer;

import java.util.ArrayList;
import java.util.List;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
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
import com.mycompany.app.messages.SwiftMTReplyMessage;

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

    @JmsListener(destination = "#{@artemisConfigLan.queue}", selector = "#{@artemisConfigLan.selector}", containerFactory = "lanBatchJmsListenerContainerFactory")
    @Retryable(maxAttempts = 1, backoff = @Backoff(delay = 1000))
    @Transactional
    public void processSwiftMT(BatchMessage batch, Session session) {
        List<SwiftMTMessage> messages = batch.getMessages(SwiftMTMessage.class);

        //insert messages into db, process duplicates
        int[] batchResult = aggregatorRepository.insertBatch(messages);
        List<SwiftMTMessage> ignoredMessages = processIgnored(messages, batchResult);
        log.info("Inserted {}, skipped {} records", messages.size(), ignoredMessages.size());

        //envelop non-dups into ED, generate EdNo and send to the desination
        if (messages.size() > 0) {
            Long edno = aggregatorRepository.getNextEdNo();
            log.info("Generated edno: {}", edno);
            prcsJmsTemplate.send("ED", messageCreator -> {
                return messageCreator.createTextMessage(SwiftMTHelper.createED503(messages, edno));
            });
            log.info("Sent ED503 message", messages.size());
        }

        //send status to the replyTo queue
        sendReplyMessages(session, messages, ignoredMessages);
        log.info("Sent replies");
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

    void sendReplyMessages(Session session, List<SwiftMTMessage> messages, List<SwiftMTMessage> ignoredMessages) { 
        try {
            //positive responses
            for(SwiftMTMessage m: messages) {
                ObjectMessage response = session.createObjectMessage(new SwiftMTReplyMessage());
                response.setJMSCorrelationID(m.getMessage().getJMSMessageID());
                session.createProducer(m.getMessage().getJMSReplyTo()).send(response);
            }
        
            //negative responses
            for(SwiftMTMessage m: ignoredMessages) {
                ObjectMessage response = session.createObjectMessage(new SwiftMTReplyMessage(1, "Duplicate"));
                response.setJMSCorrelationID(m.getMessage().getJMSMessageID());
                session.createProducer(m.getMessage().getJMSReplyTo()).send(response);
            }
        } catch(JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
