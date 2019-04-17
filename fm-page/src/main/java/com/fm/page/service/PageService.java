package com.fm.page.service;

import com.fm.common.enums.ExceptionEnum;
import com.fm.common.exception.FmException;
import com.fm.item.pojo.*;
import com.fm.page.client.BrandClient;
import com.fm.page.client.CategoryClient;
import com.fm.page.client.GoodsClient;
import com.fm.page.client.SpecClient;
import com.fm.page.mapper.FootPrintMapper;
import com.fm.page.pojo.FootPrint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

@Slf4j
@Service
public class PageService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecClient specClient;

    @Autowired
    private FootPrintMapper footPrintMapper;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${fm.page.path}")
    private String dest;

    public Map<String, Object> loadModelAndFootPrint(Long spuId,Long userId) {

        Map<String, Object> map = loadModel(spuId);

        //添加用户痕迹
        FootPrint fp = new FootPrint();

        fp.setUserId(0L);
        if (userId != null)
            fp.setUserId(userId);

        fp.setSpuId(spuId);
        SpuDetail detail = (SpuDetail)map.get("detail");
        fp.setGenericSpec(detail.getGenericSpec());
        fp.setSpecialSpec(detail.getSpecialSpec());
        fp.setAddTime(new Date());
        fp.setDeleted(false);
        footPrintMapper.insert(fp);

        return map;
    }
    public Map<String, Object> loadModel(Long spuId) {
        Map<String, Object> model = new HashMap<>();

        Spu spu = goodsClient.querySpuBySpuId(spuId);

        //上架未上架，则不应该查询到商品详情信息，抛出异常
        if (!spu.getSaleable()) {
            throw new FmException(ExceptionEnum.GOODS_NOT_SALEABLE);
        }

        SpuDetail detail = spu.getSpuDetail();
        List<Sku> skus = spu.getSkus();
        Brand brand = brandClient.queryById(spu.getBrandId());
        //查询三级分类
        List<Category> categories = categoryClient.queryByIds(
                Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        List<SpecGroup> specs = specClient.querySpecsByCid(spu.getCid3());
        //页面只需要spu里面的title和subTitle
//        model.put("spu", spu);
        model.put("title", spu.getTitle());
        model.put("subTitle", spu.getSubTitle());
        model.put("brand", brand);
        model.put("categories", categories);
        model.put("skus", skus);
        model.put("detail", detail);
        model.put("specs", specs);

        return model;
    }

    public  void createHtml(Long spuId) {
        //上下文
        Context context = new Context();
        Map<String, Object> map = loadModel(spuId);
        context.setVariables(map);

        //输出流
        File file = new File(this.dest, spuId + ".html");
        //如果页面存在，先删除，后进行创建静态页
        if (file.exists()) {
            file.delete();
        }
        try (PrintWriter writer = new PrintWriter(file, "utf-8")) {
            templateEngine.process("item", context, writer);
        } catch (Exception e) {
            log.error("【静态页服务】生成静态页面异常", e);
        }
    }

    public void deleteHtml(Long id) {
        File file = new File(this.dest + id + ".html");
        if (file.exists()) {
            boolean flag = file.delete();
            if (!flag) {
                log.error("删除静态页面失败");
            }
        }
    }
}
