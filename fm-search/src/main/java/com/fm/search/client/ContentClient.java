package com.fm.search.client;



import com.fm.search.dto.ContentDTO;

import java.util.*;

public abstract class ContentClient {
    public static final Map<String,Object> countMap = new HashMap<String,Object>(){
        {
            //轮播
            List<ContentDTO>  carouselList = new ArrayList<>();
            carouselList.add(new ContentDTO("img/banner1.jpg"));
            carouselList.add(new ContentDTO("img/banner2.jpg"));
            carouselList.add(new ContentDTO("img/banner3.jpg"));
            carouselList.add(new ContentDTO("img/banner4.jpg"));
            put("carousel",carouselList);
            //今日推荐
            List<ContentDTO>  recommendList = new ArrayList<>();
            recommendList.add(new ContentDTO("img/today01.png"));
            recommendList.add(new ContentDTO("img/today02.png"));
            recommendList.add(new ContentDTO("img/today03.png"));
            recommendList.add(new ContentDTO("img/today04.png"));
            put("todayRecommend",recommendList);
            //猜你喜欢
            List<ContentDTO>  forecastList = new ArrayList<>();
            forecastList.add(new ContentDTO("img/like_01.png","跑步机垫减震垫SH-运动垫","¥116.00"));
            forecastList.add(new ContentDTO("img/like_02.png","健身棒-入门级别","¥60.00"));
            forecastList.add(new ContentDTO("img/like_03.png","健身重力壶","¥26.00"));
            forecastList.add(new ContentDTO("img/like_04.png","B3656S动感单车","¥86.00"));
            forecastList.add(new ContentDTO("img/like_05.png","SH-G5890罗马椅","¥76.00"));
            forecastList.add(new ContentDTO("img/like_06.png","SH-34004 瑜伽普拉提套装","¥176.00"));
            put("forecast",forecastList);
            //楼层
            List<ContentDTO>  floor1 = new ArrayList<>();
            floor1.add(new ContentDTO("img/floor-1-1.png"));
            floor1.add(new ContentDTO("img/floor-1-2.png"));
            floor1.add(new ContentDTO("img/floor-1-3.png"));
            floor1.add(new ContentDTO("img/floor-1-4.png"));
            floor1.add(new ContentDTO("img/floor-1-5.png"));
            floor1.add(new ContentDTO("img/floor-1-6.png"));
            put("floor1",floor1);
        }
    };


    public static Map<String,Object> findById(Long id){
        return countMap;
    }
}

