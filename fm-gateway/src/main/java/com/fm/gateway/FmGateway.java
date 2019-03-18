package com.fm.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringCloudApplication
public class FmGateway {
    public static void main(String[] args) {
        SpringApplication.run(FmGateway.class);
    }
}
