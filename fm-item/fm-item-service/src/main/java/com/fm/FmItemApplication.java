package com.fm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.fm.item.mapper")
public class FmItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(FmItemApplication.class, args);
    }
}
