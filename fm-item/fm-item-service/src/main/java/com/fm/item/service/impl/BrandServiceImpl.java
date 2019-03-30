package com.fm.item.service.impl;

import com.fm.common.enums.ExceptionEnum;
import com.fm.common.exception.FmException;
import com.fm.common.vo.PageResult;
import com.fm.item.mapper.BrandMapper;
import com.fm.item.service.BrandService;
import com.fm.item.pojo.Brand;
import com.fm.item.pojo.Category;
import com.fm.item.vo.BrandVo;
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
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;


    @Override
    public PageResult<Brand> queryBrandByPageAndSort(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        //开启分页
        PageHelper.startPage(page, rows);//通过mybatis的拦截器实现添加分页
        //过滤
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(key)) {
            example.createCriteria().orLike("name", "%" + key + "%")
                    .orEqualTo("letter", key);
        }
        if (StringUtils.isNotBlank(sortBy)) {
            String sortByClause = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(sortByClause);
        }
        // 查询
        List<Brand> brandList = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(brandList)) {
            throw new FmException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //通过分页助手，解析分页结果
        PageInfo<Brand> pageInfo = new PageInfo<>(brandList);
        // 返回结果
        return new PageResult<>(pageInfo.getTotal(), brandList);
    }

    // 添加事务
    @Transactional
    @Override
    public void saveBrand(Brand brand, List<Long> cids) {
        brand.setId(null);
        int resultCount = brandMapper.insert(brand);
        if (resultCount == 0) {
            throw new FmException(ExceptionEnum.BRAND_CREATE_FAILED);
        }
        //更新品牌分类表
        for (Long cid : cids) {
            // 新增brand，brand的id会自动回写
            resultCount = brandMapper.saveCategoryBrand(cid, brand.getId());

            if (resultCount == 0) {
                throw new FmException(ExceptionEnum.BRAND_CREATE_FAILED);
            }
        }
    }

    @Override
    public List<Category> queryCategoryByBid(Long bid) {
        return brandMapper.queryCategoryByBid(bid);
    }

    @Transactional
    @Override
    public void updateBrand(BrandVo brandVo) {
        Brand brand = new Brand();
        brand.setId(brandVo.getId());
        brand.setName(brandVo.getName());
        brand.setImage(brandVo.getImage());
        brand.setLetter(brandVo.getLetter());

        //更新
        int resultCount = brandMapper.updateByPrimaryKey(brand);
        if (resultCount == 0) {
            throw new FmException(ExceptionEnum.UPDATE_BRAND_FAILED);
        }
        List<Long> cids = brandVo.getCids();
        //更新品牌分类表


        brandMapper.deleteCategoryBrandByBid(brandVo.getId());

        for (Long cid : cids) {
            resultCount = brandMapper.saveCategoryBrand(cid, brandVo.getId());
            if (resultCount == 0) {
                throw new FmException(ExceptionEnum.UPDATE_BRAND_FAILED);
            }

        }


    }

    @Transactional
    @Override
    public void deleteBrand(Long bid) {
        int result = brandMapper.deleteByPrimaryKey(bid);
        if (result == 0) {
            throw new FmException(ExceptionEnum.DELETE_BRAND_EXCEPTION);
        }
        //删除中间表
        result = brandMapper.deleteCategoryBrandByBid(bid);
        if (result == 0) {
            throw new FmException(ExceptionEnum.DELETE_BRAND_EXCEPTION);
        }
    }

    @Override
    public List<Brand> queryBrandByCid(Long cid) {
        List<Brand> brandList = brandMapper.queryBrandByCid(cid);
        if (CollectionUtils.isEmpty(brandList)) {
            throw new FmException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brandList;
    }

    @Override
    public Brand queryBrandByBid(Long id) {
        Brand brand = new Brand();
        brand.setId(id);
        Brand b1 = brandMapper.selectByPrimaryKey(brand);
        if (b1 == null) {
            throw new FmException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return b1;
    }

    @Override
    public List<Brand> queryBrandByIds(List<Long> ids) {
        List<Brand> brands = brandMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(brands)) {
            throw new FmException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brands;
    }

}
