package com.fm.search.service;


import com.fm.auth.entity.UserInfo;
import com.fm.search.client.ContentClient;
import com.fm.search.filter.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ContentService {

    public Map<String,Object> queryIndexContent() {
        Map<String,Object> allContent;
        //获得用户信息
        //获取用户信息
        UserInfo user = LoginInterceptor.getLoginUser();
        if (user != null){
            //根据用户信息，定制化展示内容
            allContent = ContentClient.findById(1L);
        }else {
            allContent = ContentClient.findById(1L);
        }
        //返回内容
        return allContent;
    }
}
