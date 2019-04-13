package com.fm.item.service;



import com.fm.item.pojo.Category;
import com.fm.item.pojo.ChildCates;

import java.util.List;


public interface CategoryService {


    List<Category> queryCategoryByPid(Long pid);

    List<Category> queryCategoryByIds(List<Long> ids);

    List<Category> queryAllByCid3(Long id);

    public List<ChildCates> queryChilds(Long id);
}
