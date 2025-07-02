package com.mycompany.app.consumer;

import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

public class BatchJmsListenerContainerFactory extends DefaultJmsListenerContainerFactory {

	int batchSize;

	public BatchJmsListenerContainerFactory(int batchSize) {
		this.batchSize = batchSize;
	}

    @Override
	protected DefaultMessageListenerContainer createContainerInstance() {
		return new BatchMessageListenerContainer(batchSize);
	}
}
