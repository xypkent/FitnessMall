package com.fm.search.client;



import com.fm.search.dto.ContentDTO;
import com.fm.search.service.ContentService;

import java.util.*;

public abstract class ContentClient {

    public static Map<String,Object> findByTags(String tags){
        if (tags.equals(ContentService.DEFAULT_TAG)){
            return defaultMap;
        }
        String[] userTags = tags.split(",");
        if (userTags[0].equals(ContentService.PRIMARY_TAG)) {//入门
            return primerMap;
        }
        if (userTags[0].equals(ContentService.ADVANCE_TAG)) {//进阶
            return advanceMap;
        }
        if (userTags[0].equals(ContentService.EXTREME_SPORTS_TAG)) {//极限
            return extremeMap;
        }
        return defaultMap;
    }


    public static final Map<String,Object> defaultMap = new HashMap<String,Object>(){//默认/入门展示内容
        {
            //轮播
            List<ContentDTO>  carouselList = new ArrayList<>();
            carouselList.add(new ContentDTO("img/banner3.jpg"));
            carouselList.add(new ContentDTO("img/banner5.jpg"));
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
            forecastList.add(new ContentDTO("img/like_04.png","B3656S运动板","¥86.00"));
            forecastList.add(new ContentDTO("img/like_05.png","SH-G5890罗马椅","¥76.00"));
            forecastList.add(new ContentDTO("img/like_06.png","SH-340 瑜伽普拉提套装","¥176.00"));
            put("forecast",forecastList);
            //楼层
            List<Object>  floor1 = new ArrayList<>();
            floor1.add(new ContentDTO("img/floor-1-1.png"));
            floor1.add(new ContentDTO("img/floor-1-2.png"));
            floor1.add(new ContentDTO("img/floor-1-3.png"));
            floor1.add(new ContentDTO("img/floor-1-4.png"));
            floor1.add(new ContentDTO("img/floor-1-5.png"));
            floor1.add(new ContentDTO("img/floor-1-6.png"));
            floor1.add(new ContentDTO("img/floor-1-b02.png"));//按摩椅轮播图
            floor1.add(new ContentDTO("img/floor-1-b01.png"));
            floor1.add("健身入门");
            List<Object>  floor2 = new ArrayList<>();
            floor2.add(new ContentDTO("img/floor-2-1.png"));
            floor2.add(new ContentDTO("img/floor-2-2.png"));
            floor2.add(new ContentDTO("img/floor-2-3.png"));
            floor2.add(new ContentDTO("img/floor-2-4.png"));
            floor2.add(new ContentDTO("img/floor-2-5.png"));
            floor2.add(new ContentDTO("img/floor-2-6.png"));
            floor2.add(new ContentDTO("img/floor-1-b03.png"));
            floor2.add(new ContentDTO("img/floor-1-b01.png"));
            floor2.add("健身器械");
            put("floor1",floor1);
            put("floor2",floor2);
        }
    };
    public static final Map<String,Object> primerMap = defaultMap;//默认的就是入门展示

    public static final Map<String,Object> advanceMap = new HashMap<String,Object>(){//进阶展示
        {
            //轮播
            List<ContentDTO>  carouselList = new ArrayList<>();
            carouselList.add(new ContentDTO("img/banner1.jpg"));
            carouselList.add(new ContentDTO("img/banner2.jpg"));
            put("carousel",carouselList);

            String prefix = "img/customization/advanceMap";
            //今日推荐
            List<ContentDTO>  recommendList = new ArrayList<>();
            recommendList.add(new ContentDTO(prefix + "/today01.png"));
            recommendList.add(new ContentDTO(prefix + "/today02.png"));
            recommendList.add(new ContentDTO(prefix + "/today03.png"));
            recommendList.add(new ContentDTO(prefix + "/today04.png"));
            put("todayRecommend",recommendList);
            //猜你喜欢
            List<ContentDTO>  forecastList = new ArrayList<>();
            forecastList.add(new ContentDTO(prefix + "/like_01.png","跑步机减震SH--进阶级","¥116.00"));
            forecastList.add(new ContentDTO(prefix + "/like_02.png","健身棒-进阶级","¥60.00"));
            forecastList.add(new ContentDTO(prefix + "/like_03.png","健身重力壶","¥26.00"));
            forecastList.add(new ContentDTO(prefix + "/like_04.png","B3656S运动板","¥86.00"));
            forecastList.add(new ContentDTO(prefix + "/like_05.png","SH-G5890罗马椅-进阶级","¥76.00"));
            forecastList.add(new ContentDTO(prefix + "/like_06.png","SH-340 瑜伽普拉提套装","¥176.00"));
            put("forecast",forecastList);
            //楼层
            List<Object>  floor1 = new ArrayList<>();
            floor1.add(new ContentDTO(prefix + "/floor-1-1.png"));
            floor1.add(new ContentDTO(prefix + "/floor-1-2.png"));
            floor1.add(new ContentDTO(prefix + "/floor-1-3.png"));
            floor1.add(new ContentDTO(prefix + "/floor-1-4.png"));
            floor1.add(new ContentDTO(prefix + "/floor-1-5.png"));
            floor1.add(new ContentDTO(prefix + "/floor-1-6.png"));
            floor1.add(new ContentDTO(prefix + "/floor-1-b02.png"));//按摩椅轮播图
            floor1.add(new ContentDTO(prefix + "/floor-1-b01.png"));
            floor1.add("健身用品"); // 8
            List<Object>  floor2 = new ArrayList<>();
            floor2.add(new ContentDTO(prefix + "/floor-2-1.png"));
            floor2.add(new ContentDTO(prefix + "/floor-2-2.png"));
            floor2.add(new ContentDTO(prefix + "/floor-2-3.png"));
            floor2.add(new ContentDTO(prefix + "/floor-2-4.png"));
            floor2.add(new ContentDTO(prefix + "/floor-2-5.png"));
            floor2.add(new ContentDTO(prefix + "/floor-2-6.png"));
            floor2.add(new ContentDTO(prefix + "/floor-1-b03.png"));
            floor2.add(new ContentDTO(prefix + "/floor-1-b01.png"));
            floor2.add("健身进阶");
            put("floor2",floor1); //健身进阶商品排在前面
            put("floor1",floor2);
        }
    };
    public static final Map<String,Object> extremeMap = new HashMap<String,Object>(){//极限运动
        {
            //轮播
            List<ContentDTO>  carouselList = new ArrayList<>();
            carouselList.add(new ContentDTO("img/banner4.jpg"));
            carouselList.add(new ContentDTO("img/banner6.jpg"));
            put("carousel",carouselList);

            String prefix = "img/customization/extremeMap";
            //今日推荐
            List<ContentDTO>  recommendList = new ArrayList<>();
            recommendList.add(new ContentDTO(prefix + "/today04.png"));
            recommendList.add(new ContentDTO(prefix + "/today01.png"));
            recommendList.add(new ContentDTO(prefix + "/today03.png"));
            recommendList.add(new ContentDTO(prefix + "/today02.png"));
            put("todayRecommend",recommendList);
            //猜你喜欢
            List<ContentDTO>  forecastList = new ArrayList<>();
            forecastList.add(new ContentDTO(prefix + "/like_01.png","跑步机垫减震垫SH-运动垫","¥116.00"));
            forecastList.add(new ContentDTO(prefix + "/like_02.png","健身棒-极限级","¥60.00"));
            forecastList.add(new ContentDTO(prefix + "/like_03.png","健身重力壶","¥26.00"));
            forecastList.add(new ContentDTO(prefix + "/like_04.png","B3656S运动板","¥86.00"));
            forecastList.add(new ContentDTO(prefix + "/like_05.png","SH-G5890罗马椅","¥76.00"));
            forecastList.add(new ContentDTO(prefix + "/like_06.png","SH-340 瑜伽普拉提套装","¥176.00"));
            put("forecast",forecastList);
            //楼层
            List<Object>  floor1 = new ArrayList<>();
            floor1.add(new ContentDTO(prefix + "/floor-1-1.png"));
            floor1.add(new ContentDTO(prefix + "/floor-1-2.png"));
            floor1.add(new ContentDTO(prefix + "/floor-1-3.png"));
            floor1.add(new ContentDTO(prefix + "/floor-1-4.png"));
            floor1.add(new ContentDTO(prefix + "/floor-1-5.png"));
            floor1.add(new ContentDTO(prefix + "/floor-1-6.png"));
            floor1.add(new ContentDTO(prefix + "/floor-1-b03.png"));
            floor1.add(new ContentDTO(prefix + "/floor-1-b01.png"));
            floor1.add("极限健身"); // 8
            put("floor1",floor1);
            List<Object>  floor2 = new ArrayList<>();
            floor2.add(new ContentDTO(prefix + "/floor-2-1.png"));
            floor2.add(new ContentDTO(prefix + "/floor-2-2.png"));
            floor2.add(new ContentDTO(prefix + "/floor-2-3.png"));
            floor2.add(new ContentDTO(prefix + "/floor-2-4.png"));
            floor2.add(new ContentDTO(prefix + "/floor-2-5.png"));
            floor2.add(new ContentDTO(prefix + "/floor-2-6.png"));
            floor2.add(new ContentDTO(prefix + "/floor-1-b02.png"));//按摩椅轮播图
            floor2.add(new ContentDTO(prefix + "/floor-1-b04.png"));
            floor2.add("健身器械"); // 8
            put("floor2",floor2);
        }
    };

}

