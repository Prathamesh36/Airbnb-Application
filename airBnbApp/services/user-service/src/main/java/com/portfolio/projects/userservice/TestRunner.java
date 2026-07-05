package com.portfolio.projects.userservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestRunner implements CommandLineRunner {

    @Value("${spring.datasource.url:MISSING}")
    private String url;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=========================================");
        System.out.println("DATASOURCE URL IS: " + url);
        System.out.println("=========================================");
    }
}
