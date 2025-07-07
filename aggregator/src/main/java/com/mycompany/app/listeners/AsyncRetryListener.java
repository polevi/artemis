package com.mycompany.app.listeners;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component("retryListener")
@Slf4j
public class AsyncRetryListener implements RetryListener {
    
    @Override
    public <T,E extends Throwable> void onError(RetryContext context, RetryCallback<T,E> callback, Throwable throwable) {
        log.error("{}: {}", throwable.getCause(),   throwable.getMessage());
    }
}
