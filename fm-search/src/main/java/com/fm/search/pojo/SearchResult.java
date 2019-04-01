package com.fm.search.pojo;


import com.fm.common.vo.PageResult;
import com.fm.item.pojo.Brand;
import com.fm.item.pojo.Category;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SearchResult<Goods> extends PageResult<Goods> {

    private List<Brand> brands;
    private List<Category> categories;
    //规格参数 key及待选项 过滤条件
    private List<Map<String, Object>> specs;

    //可以优化成使用工厂模式构建对象
    public SearchResult(Long total,
                        Integer totalPage,
                        List<Goods> items,
                        List<Category> categories,
                        List<Brand> brands,
                        List<Map<String, Object>> specs) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Map<String, Object>> getSpecs() {
        return specs;
    }

    public void setSpecs(List<Map<String, Object>> specs) {
        this.specs = specs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SearchResult<?> that = (SearchResult<?>) o;
        return Objects.equals(brands, that.brands) &&
                Objects.equals(categories, that.categories) &&
                Objects.equals(specs, that.specs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), brands, categories, specs);
    }
}
