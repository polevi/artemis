package com.mycompany.app.consumer;

import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

public class BatchJmsListenerContainerFactory extends DefaultJmsListenerContainerFactory {

    @Override
	protected DefaultMessageListenerContainer createContainerInstance() {
		return new BatchMessageListenerContainer();
	}
}
