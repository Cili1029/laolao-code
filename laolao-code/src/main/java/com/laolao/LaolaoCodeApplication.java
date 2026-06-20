package com.laolao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LaolaoCodeApplication {
    public static void main(String[] args) {
        SpringApplication.run(LaolaoCodeApplication.class, args);
    }

}
