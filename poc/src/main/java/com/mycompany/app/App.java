package com.mycompany.app;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class App implements CommandLineRunner {

    @Autowired
    private RawDataRepository rawDataRepository;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        List<Object[]> list = new ArrayList<Object[]>();

        for (int i = 0; i < 10; i++) {
            list.add(new Object[] { i, LocalDate.now(), "Hello world" + i });
        }
        
        list.add(new Object[] { 0, LocalDate.now(), "aaaaaa" });

        rawDataRepository.insertBatch(list);
    }
}
