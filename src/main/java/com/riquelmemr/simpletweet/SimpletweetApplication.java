package com.riquelmemr.simpletweet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SimpletweetApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpletweetApplication.class, args);
    }
}
