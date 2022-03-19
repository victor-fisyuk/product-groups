package com.victorfisyuk.productgroups;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class ProductGroupsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductGroupsApplication.class, args);
    }

}
