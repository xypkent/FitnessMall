package com.fm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(value = "com.fm.user.mapper")
public class FmUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(FmUserApplication.class);
    }
}
