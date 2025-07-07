package com.mycompany.app.consumer;

import org.springframework.jms.listener.DefaultMessageListenerContainer;
import jakarta.jms.*;

public class BatchMessageListenerContainer extends DefaultMessageListenerContainer {

    int batchSize;

    public BatchMessageListenerContainer(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    protected Message receiveMessage(MessageConsumer consumer) throws JMSException {
        BatchMessage batch = new BatchMessage(batchSize);
        while (!batch.releaseAfterMessage(getMessageConverter().fromMessage(super.receiveMessage(consumer)))) ;
        return batch.isEmpty() ? null : batch;
    }
}
