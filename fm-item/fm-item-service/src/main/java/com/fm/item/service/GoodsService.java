package com.fm.item.service;


import com.fm.common.dto.CartDto;
import com.fm.common.vo.PageResult;
import com.fm.item.pojo.Sku;
import com.fm.item.pojo.Spu;
import com.fm.item.pojo.SpuDetail;

import java.util.List;


public interface GoodsService {
    PageResult<Spu> querySpuByPage(Integer page, Integer rows, String key, Boolean saleable);

    SpuDetail querySpuDetailBySpuId(Long spuId);

    List<Sku> querySkuBySpuId(Long spuId);

    void deleteGoodsBySpuId(Long spuId);

    void addGoods(Spu spu);

    void updateGoods(Spu spu);

    void handleSaleable(Spu spu);

    Spu querySpuBySpuId(Long spuId);

    List<Sku> querySkusByIds(List<Long> ids);

    void decreaseStock(List<CartDto> cartDtos);
}
