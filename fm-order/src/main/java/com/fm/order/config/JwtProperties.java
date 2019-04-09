package com.fm.order.config;

import com.fm.auth.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@Data
@Slf4j
@ConfigurationProperties(prefix = "fm.jwt")
public class JwtProperties {

    private String pubKeyPath;

    private String cookieName;

    private PublicKey publicKey;//公钥

    @PostConstruct//对象一旦实例化后，就应该读取公钥和私钥
    public void init() {
        try {
            publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            log.error("初始化公钥失败", e);
            throw new RuntimeException();

        }
    }
}
