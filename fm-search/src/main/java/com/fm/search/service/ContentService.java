package com.fm.search.service;


import com.fm.auth.entity.UserInfo;
import com.fm.common.utils.JsonUtils;
import com.fm.foot.mapper.FootPrintMapper;
import com.fm.foot.pojo.FootPrint;
import com.fm.search.client.ContentClient;
import com.fm.search.filter.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ContentService {

    public static final String[] CROWDS = {"健身入门","健身进阶","极限运动"};
    public static final String DEFAULT_TAG = "默认";
    public static final String PRIMARY_TAG = CROWDS[0];
    public static final String ADVANCE_TAG = CROWDS[1];
    public static final String EXTREME_SPORTS_TAG = CROWDS[2];

    @Autowired
    private FootPrintMapper footPrintMapper;

    public Map<String,Object> queryIndexContent() {
        //获取用户信息
        UserInfo user = LoginInterceptor.getLoginUser();
        if (user != null){
            //根据用户信息，定制化展示内容
            String userTag = user.getTag();//获得用户标签
            //获得用户最近浏览痕迹
            List<FootPrint> fps = footPrintMapper.queryWeekFootPrintByUid(user.getId());
            //痕迹不为空并大于5
            if (!CollectionUtils.isEmpty(fps) && fps.size() > 5){
                //分析最近喜好
                userTag = analyzeRecentPreferences(fps);
            }
            if (StringUtils.isNotBlank(userTag)){//根据用户标签展示内容
                return ContentClient.findByTags(userTag);
            }
        }
        //走默认展示
        return ContentClient.findByTags(DEFAULT_TAG);
    }

    //分析最近喜好
    private String analyzeRecentPreferences(List<FootPrint> fps) {
        int[] record = new int[5];
        for (FootPrint fp : fps) {
            Map<Long, String> map = JsonUtils.toMap(fp.getGenericSpec(), Long.class, String.class);
            String crowd = map.get(8L);//适用人群

            for (int i = 0; i< CROWDS.length ;i++) {
                if (CROWDS[i].equals(crowd))
                    record[i]++;
            }
        }
        int max = 0,index = 0;
        for (int i = 0; i< record.length ;i++){
            if (record[i] > max){
                max = record[i];
                index = i;
            }
        }
        return max >= 5 ? CROWDS[index]: DEFAULT_TAG;
    }
}
