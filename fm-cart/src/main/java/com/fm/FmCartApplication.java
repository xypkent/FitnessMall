package com.fm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
public class FmCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(FmCartApplication.class, args);
    }
}
