package com.fm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

//@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@MapperScan("com.fm.page.mapper")
public class FmPageApplication {
    public static void main(String[] args) {
        SpringApplication.run(FmPageApplication.class);
    }
}

