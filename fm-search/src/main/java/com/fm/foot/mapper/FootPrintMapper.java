package com.fm.foot.mapper;


import com.fm.common.mapper.BaseMapper;
import com.fm.foot.pojo.FootPrint;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface FootPrintMapper extends BaseMapper<FootPrint,Long> {

    /**
     * 根据用户id,查询用户近七天的痕迹
     * @param uid
     * @return
     */
    @Select("select * from tb_footprint where user_id = #{uid} and  YEARWEEK(DATE_FORMAT(add_time,'%Y-%m-%d')) = YEARWEEK(NOW())")
    List<FootPrint> queryWeekFootPrintByUid(Long uid);

}
