package com.fm.auth.service;

import com.fm.auth.client.UserClient;
import com.fm.auth.entity.UserInfo;
import com.fm.auth.properties.JwtProperties;
import com.fm.auth.utils.JwtUtils;
import com.fm.common.enums.ExceptionEnum;
import com.fm.common.exception.FmException;
import com.fm.user.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties props;


    public String login(String username, String password) {
        try {
            User user = userClient.queryUser(username, password);
            if (user == null) {
                return null;
            }
            UserInfo userInfo = new UserInfo(user.getId(), user.getUsername());
            //生成Token
            String token = JwtUtils.generateToken(userInfo, props.getPrivateKey(), props.getExpire());
            return token;
        } catch (Exception e) {
            log.error("【授权中心】用户名和密码错误，用户名：{}", username,e);
            throw new FmException(ExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
        }
    }
}
