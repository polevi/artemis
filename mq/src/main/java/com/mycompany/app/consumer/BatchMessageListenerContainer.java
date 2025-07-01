package com.mycompany.app.consumer;

import org.springframework.jms.listener.DefaultMessageListenerContainer;
import jakarta.jms.*;

public class BatchMessageListenerContainer extends DefaultMessageListenerContainer {
    public static final int DEFAULT_BATCH_SIZE = 1000;

    @Override
    protected Message receiveMessage(MessageConsumer consumer) throws JMSException {
        BatchMessage batch = new BatchMessage(DEFAULT_BATCH_SIZE);
        while (!batch.releaseAfterMessage(getMessageConverter().fromMessage(super.receiveMessage(consumer)))) ;
        return batch.isEmpty() ? null : batch;
    }
}
