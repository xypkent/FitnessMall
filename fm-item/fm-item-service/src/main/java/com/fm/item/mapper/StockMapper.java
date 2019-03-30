package com.fm.item.mapper;


import com.fm.common.mapper.BaseMapper;
import com.fm.item.pojo.Stock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface StockMapper extends BaseMapper<Stock,Long> {

    @Update("update tb_stock set stock = stock - #{num} where sku_id = #{skuId} and stock >= #{num}")
    int decreaseStock(@Param("skuId") Long skuId, @Param("num") Integer num);
}
