package com.fm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients//远程服务调用
@MapperScan("com.fm.foot.mapper")
public class FmSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(FmSearchApplication.class);
    }
}
