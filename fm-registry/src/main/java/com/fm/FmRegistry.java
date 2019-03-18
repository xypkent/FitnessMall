package com.fm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class FmRegistry {
    public static void main(String[] args) {
        SpringApplication.run(FmRegistry.class, args);
    }
}
