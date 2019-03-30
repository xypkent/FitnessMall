package com.fm.item.service;



import com.fm.common.vo.PageResult;
import com.fm.item.pojo.Brand;
import com.fm.item.pojo.Category;
import com.fm.item.vo.BrandVo;

import java.util.List;


public interface BrandService {

    PageResult<Brand> queryBrandByPageAndSort(Integer page, Integer rows, String sortBy, Boolean desc, String key);

    void saveBrand(Brand brand, List<Long> cids);

    List<Category> queryCategoryByBid(Long bid);

    void updateBrand(BrandVo brandVo);

    void deleteBrand(Long bid);

    List<Brand> queryBrandByCid(Long cid);

    Brand queryBrandByBid(Long id);

    List<Brand> queryBrandByIds(List<Long> ids);

}
