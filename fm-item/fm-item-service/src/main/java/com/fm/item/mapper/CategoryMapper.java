package com.fm.item.mapper;

import com.fn.item.pojo.Category;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;


//另外加入通用Mapper接口,根据idList操作
public interface CategoryMapper extends Mapper<Category>, IdListMapper<Category, Long> {
}
