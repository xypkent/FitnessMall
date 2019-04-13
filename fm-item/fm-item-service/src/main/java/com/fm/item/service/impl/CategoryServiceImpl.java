package com.fm.item.service.impl;

import com.fm.common.enums.ExceptionEnum;
import com.fm.common.exception.FmException;
import com.fm.item.mapper.CategoryMapper;
import com.fm.item.pojo.ChildCates;
import com.fm.item.service.CategoryService;
import com.fm.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;


    @Override
    public List<Category> queryCategoryByPid(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        List<Category> categoryList = categoryMapper.select(category);
        if (CollectionUtils.isEmpty(categoryList)) {
            throw new FmException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return categoryList;
    }

    @Override
    public List<Category> queryCategoryByIds(List<Long> ids) {
        List<Category> categoryList = categoryMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(categoryList)) {
            throw new FmException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return categoryList;
    }

    @Override
    public List<Category> queryAllByCid3(Long id) {
        Category c3 = categoryMapper.selectByPrimaryKey(id);
        Category c2 = categoryMapper.selectByPrimaryKey(c3.getParentId());
        Category c1 = categoryMapper.selectByPrimaryKey(c2.getParentId());
        List<Category> list = Arrays.asList(c1, c2, c3);
        if (CollectionUtils.isEmpty(list)) {
            throw new FmException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return list;
    }

    public List<ChildCates> queryChilds(Long id) {
        //查询二级分类
        List<Category> c2s = queryCategoryByPid(id);
        //创建一个集合用来封装所有二、三级分类
        List<ChildCates> childCatess = new ArrayList<>();
        //遍历二级分类封装数据，并查询三级分类
        for (Category c2 : c2s) {
            //创建对象封装数据
            ChildCates childCates = new ChildCates();
            childCates.setId(c2.getId());
            childCates.setParentId(c2.getParentId());
            childCates.setIsParent(c2.getIsParent());
            childCates.setName(c2.getName());
            childCates.setSort(c2.getSort());
            //查询旗下所有的三级分类
            List<Category> c3s = queryCategoryByPid(c2.getId());
            childCates.setChildCates(c3s);
            childCatess.add(childCates);
        }
        return childCatess;
    }
}
