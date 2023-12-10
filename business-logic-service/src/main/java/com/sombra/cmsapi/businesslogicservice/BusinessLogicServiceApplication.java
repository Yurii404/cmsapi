package com.sombra.cmsapi.businesslogicservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BusinessLogicServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessLogicServiceApplication.class, args);
    }

}
