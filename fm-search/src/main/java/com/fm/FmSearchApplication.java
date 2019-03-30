package com.fm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients//远程服务调用
public class FmSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(FmSearchApplication.class);
    }
}
