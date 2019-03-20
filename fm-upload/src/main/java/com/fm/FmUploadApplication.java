package com.fm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: Administrator
 * @Date: 2019/03/20 17:49
 * @Description: 文件上传启动类
 */
@EnableDiscoveryClient
@SpringBootApplication
public class FmUploadApplication {
    public static void main(String[] args) {
        SpringApplication.run(FmUploadApplication.class);
    }
}
