package com.fm.item.service.impl;

import com.fm.common.enums.ExceptionEnum;
import com.fm.common.exception.FmException;
import com.fm.item.mapper.CategoryMapper;
import com.fm.item.service.CategoryService;
import com.fm.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
}
