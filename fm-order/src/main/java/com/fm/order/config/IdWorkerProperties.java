package com.fm.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "fm.worker")
public class IdWorkerProperties {

    private long workerId;// 当前机器id
    private long dataCenterId;// 序列号
}