package com.mycompany.app;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
public class ShutdownHook implements DisposableBean {

    private boolean isTerminating = false;

    @Override
    public void destroy() throws Exception {
        System.out.println("Halted abruptly, terminating gracefully..");
        isTerminating = true;
        //Thread.sleep(3000);
    }

    public boolean isTerminating() {
        return isTerminating; 
    }
}
