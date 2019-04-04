package com.fm.item.service.impl;

import com.fm.common.enums.ExceptionEnum;
import com.fm.common.exception.FmException;
import com.fm.common.vo.PageResult;
import com.fm.item.mapper.*;
import com.fm.item.pojo.*;
import com.fm.item.service.GoodsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, String key, Boolean saleable) {
        //分页
        PageHelper.startPage(page, rows);
        //过滤
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        if (saleable != null) {
            criteria.orEqualTo("saleable", saleable);
        }
        //默认以上一次更新时间排序
        example.setOrderByClause("last_update_time desc");

        //只查询未删除的商品
        criteria.andEqualTo("valid", 1);

        //查询
        List<Spu> spuList = spuMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(spuList)) {
            throw new FmException(ExceptionEnum.SPU_NOT_FOUND);
        }
        //对查询结果中的分类名和品牌名进行处理
        handleCategoryAndBrand(spuList);

        PageInfo<Spu> pageInfo = new PageInfo<>(spuList);

        return new PageResult<>(pageInfo.getTotal(), spuList);
    }

    @Override
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(spuId);
        if (spuDetail == null) {
            throw new FmException(ExceptionEnum.SPU_NOT_FOUND);
        }
        return spuDetail;
    }

    @Override
    public List<Sku> querySkuBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skuList = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(skuList)) {
            throw new FmException(ExceptionEnum.SKU_NOT_FOUND);
        }

        //查询库存
//        for (Sku sku1 : skuList) {
//            Integer stock = stockMapper.selectByPrimaryKey(sku1.getId()).getStock();
//            if(stock==null){
//                throw new FmException(ExceptionEnum.STOCK_NOT_FOUND);
//            }
//            sku1.setStock(stock);
//        }

        //查询库存
        List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
        List<Stock> stockList = stockMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(stockList)){
            throw new FmException(ExceptionEnum.STOCK_NOT_FOUND);
        }
        //把List转换成Map<SkuId,库存值>
        Map<Long, Integer> stockMap = stockList.stream().
                collect(Collectors.toMap(Stock::getSkuId, Stock::getStock));
        skuList.forEach(s -> s.setStock(stockMap.get(s.getId())));
        return skuList;
    }

    @Transactional
    @Override
    public void deleteGoodsBySpuId(Long spuId) {
        if (spuId == null) {
            throw new FmException(ExceptionEnum.INVALID_PARAM);
        }
        //删除spu,把spu中的valid字段设置成false
        Spu spu = new Spu();
        spu.setId(spuId);
        spu.setValid(false);
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count == 0) {
            throw new FmException(ExceptionEnum.DELETE_GOODS_ERROR);
        }

        //发送消息
        sendMessage(spuId, "delete");
    }

    @Transactional
    @Override
    public void addGoods(Spu spu) {
        //添加商品要添加四个表 spu, spuDetail, sku, stock四张表
        spu.setSaleable(true);
        spu.setValid(true);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        //插入数据
        int count = spuMapper.insert(spu);
        if (count != 1) {
            throw new FmException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        //插入spuDetail数据
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        count = spuDetailMapper.insert(spuDetail);
        if (count != 1) {
            throw new FmException(ExceptionEnum.GOODS_SAVE_ERROR);
        }

        //插入sku和库存
        saveSkuAndStock(spu);

        //发送消息
        sendMessage(spu.getId(), "insert");

    }

    @Transactional
    @Override
    public void updateGoods(Spu spu) {
        if (spu.getId() == 0) {
            throw new FmException(ExceptionEnum.INVALID_PARAM);
        }
        //首先查询sku
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        List<Sku> skuList = skuMapper.select(sku);
        //存在则删除
        if (!CollectionUtils.isEmpty(skuList)) {
            //删除所有sku
            skuMapper.delete(sku);
            //删除库存
            List<Long> ids = skuList.stream()
                    .map(Sku::getId)
                    .collect(Collectors.toList());
            stockMapper.deleteByIdList(ids);
        }

        //更新数据库spu,带过来的spu不带这些字段
        spu.setValid(null);//默认不删除
        spu.setSaleable(null);//默认销售状态
        spu.setCreateTime(null);
        spu.setLastUpdateTime(new Date());
        //更新spu
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count == 0) {
            throw new FmException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }
        //更新 spuDetail
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        count = spuDetailMapper.updateByPrimaryKeySelective(spuDetail);
        if (count == 0) {
            throw new FmException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }

        //更新sku和stock
        saveSkuAndStock(spu);

        //发送消息
        sendMessage(spu.getId(), "update");
    }

    @Override
    public void handleSaleable(Spu spu) {
        spu.setSaleable(!spu.getSaleable());
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count != 1) {
            throw new FmException(ExceptionEnum.UPDATE_SALEABLE_ERROR);
        }
    }

    @Override
    public Spu querySpuBySpuId(Long spuId) {
        //根据spuId查询spu
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if(spu == null){
            throw new FmException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //查询spuDetail
        SpuDetail detail = querySpuDetailBySpuId(spuId);

        //查询skus
        List<Sku> skus = querySkuBySpuId(spuId);

        spu.setSpuDetail(detail);
        spu.setSkus(skus);

        return spu;

    }

//    @Override
//    public List<Sku> querySkusByIds(List<Long> ids) {
//        List<Sku> skus = skuMapper.selectByIdList(ids);
//        if (CollectionUtils.isEmpty(skus)) {
//            throw new FmException(ExceptionEnum.GOODS_NOT_FOUND);
//        }
//        //填充库存
//        fillStock(ids, skus);
//        return skus;
//    }

//    @Transactional
//    @Override
//    public void decreaseStock(List<CartDto> cartDtos) {
//        for (CartDto cartDto : cartDtos) {
//            int count = stockMapper.decreaseStock(cartDto.getSkuId(), cartDto.getNum());
//            if (count != 1) {
//                throw new FmException(ExceptionEnum.STOCK_NOT_ENOUGH);
//            }
//        }
//    }

//    private void fillStock(List<Long> ids, List<Sku> skus) {
//        //批量查询库存
//        List<Stock> stocks = stockMapper.selectByIdList(ids);
//        if (CollectionUtils.isEmpty(stocks)) {
//            throw new FmException(ExceptionEnum.STOCK_NOT_FOUND);
//        }
//        //首先将库存转换为map，key为sku的ID
//        Map<Long, Integer> map = stocks.stream().collect(Collectors.toMap(s -> s.getSkuId(), s -> s.getStock()));
//
//        //遍历skus，并填充库存
//        for (Sku sku : skus) {
//            sku.setStock(map.get(sku.getId()));
//        }
//    }


    /**
     * 保存sku和库存
     *
     * @param spu
     */
    private void saveSkuAndStock(Spu spu) {
        List<Sku> skuList = spu.getSkus();
        List<Stock> stocks = new ArrayList<>();

        for (Sku sku : skuList) {
            sku.setSpuId(spu.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            int count = skuMapper.insert(sku);
            if (count != 1) {
                throw new FmException(ExceptionEnum.GOODS_SAVE_ERROR);
            }

            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stocks.add(stock);
        }
        //批量插入库存数据
        int count = stockMapper.insertList(stocks);
        if (count == 0) {
            throw new FmException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
    }


    /**
     * 处理商品分类名和品牌名
     *
     * @param spuList
     */
    private void handleCategoryAndBrand(List<Spu> spuList) {
        for (Spu spu : spuList) {
            //根据spu中的分类ids查询分类名
            List<String> nameList = categoryMapper.selectByIdList(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                    .stream()
                    .map(Category::getName)//Java8新特性
                    .collect(Collectors.toList());
            //对分类名进行处理
            spu.setCname(StringUtils.join(nameList, "/"));

            //查询品牌
            spu.setBname(brandMapper.selectByPrimaryKey(spu.getBrandId()).getName());
        }
    }

    /**
     * 封装发送到消息队列的方法
     *
     * @param id
     * @param type
     */
    private void sendMessage(Long id, String type) {
        try {
            amqpTemplate.convertAndSend("item." + type, id);
        } catch (Exception e) {
            log.error("{}商品消息发送异常，商品ID：{}", type, id, e);
        }
    }
}
