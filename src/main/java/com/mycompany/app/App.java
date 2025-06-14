package com.mycompany.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class App implements CommandLineRunner {

    @Autowired
    private ApplicationContext context;

    @Value("${serviceToStart}")
    private String serviceToStart;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("App started.. Looking for service '{}' ..", serviceToStart);
        ServiceRunner service = (ServiceRunner)context.getBean(serviceToStart);
        if (service != null)
            service.run();
    }
}
