package com.fm.search.service;


import com.fm.auth.entity.UserInfo;
import com.fm.search.client.ContentClient;
import com.fm.search.filter.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class ContentService {

    public static final String DEFAULT_TAG = "默认";
    public static final String PRIMARY_TAG = "健身入门";
    public static final String ADVANCE_TAG = "健身进阶";
    public static final String EXTREME_SPORTS_TAG = "极限运动";

    public Map<String,Object> queryIndexContent() {
        //获得用户信息
        //获取用户信息
        UserInfo user = LoginInterceptor.getLoginUser();
        if (user != null){
            //根据用户信息，定制化展示内容
            String userTag = user.getTag();//获得用户标签
            if (StringUtils.isNotBlank(userTag)){//根据用户标签展示内容
                return ContentClient.findByTags(userTag);
            }
        }
        //走默认展示
        return ContentClient.findByTags(DEFAULT_TAG);
    }
}
