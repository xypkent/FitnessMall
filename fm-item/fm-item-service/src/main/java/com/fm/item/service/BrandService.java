package com.fm.item.service;

import com.fm.common.enums.ExceptionEnum;
import com.fm.common.exception.FmException;
import com.fm.common.vo.PageResult;
import com.fm.item.mapper.BrandMapper;
import com.fn.item.pojo.Brand;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandByPageAndSort(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        // 开始分页
        PageHelper.startPage(page, rows);//通过mybatis的拦截器实现添加分页
        // 过滤
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(key)) {
            example.createCriteria().orLike("name", "%" + key + "%")
                    .orEqualTo("letter", key.toUpperCase());
        }
        if (StringUtils.isNotBlank(sortBy)) {
            // 排序
            String orderByClause = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);
        }
        // 查询
        List<Brand> brands = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(brands)) {
            throw new FmException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        //通过分页助手，解析分页结果
        PageInfo<Brand> brandPageInfo = new PageInfo<>(brands);

        // 返回结果
        return new PageResult<>(brandPageInfo.getTotal(), brands);
    }


    // 添加事务
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {

        brand.setId(null);
        int count = brandMapper.insert(brand);
        if (count != 1) {
            throw new FmException(ExceptionEnum.BRAND_SAVE_ERROR);
        }

        // 新增中间表
        for (Long cid : cids) {
            // 新增brand，brand的id会自动回写
            int count1 = brandMapper.insertCategoryBrand(cid, brand.getId());
            if (count1 != 1) {
                throw new FmException(ExceptionEnum.CATEGORY_NOT_FOUND);
            }
        }
    }


    public Brand queryById(Long id) {
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if (brand == null) {
            throw new FmException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brand;
    }

    public List<Brand> queryBrandByCid(Long cid) {
        List<Brand> list = brandMapper.queryByCategoryId(cid);
        if (CollectionUtils.isEmpty(list)) {
            throw new FmException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return list;

    }

    public List<Brand> queryBrandByIds(List<Long> ids) {
        List<Brand> list = brandMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(list)) {
            throw new FmException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return list;
    }
}
