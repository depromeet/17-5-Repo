package com.ogd.stockdiary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class StockDiaryApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockDiaryApplication.class, args);
    }

}
