package com.fm.item.web;

import com.fm.common.vo.PageResult;
import com.fm.item.service.GoodsService;
import com.fn.item.pojo.Sku;
import com.fn.item.pojo.Spu;
import com.fn.item.pojo.SpuDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class GoodsController {


    @Autowired
    private GoodsService goodsService;

    /**
     * 分页查询SPU
     * @param page
     * @param rows
     * @param key
     * @param saleable
     * @return
     */
    @GetMapping("spu/page")
    public ResponseEntity<PageResult<Spu>> querySpuByPage(
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "saleable", required = false)Boolean saleable) {
        return ResponseEntity.ok(goodsService.querySpuByPage(page,rows,key,saleable));
    }

    /**
     * 查询spu详情
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("spuId") Long spuId) {
        return ResponseEntity.ok(goodsService.querySpuDetailBySpuId(spuId));
    }

    /**
     * 根据spuId查询下面所有的sku
     * @param id
     * @return
     */
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long id) {
        return ResponseEntity.ok(goodsService.querySkuBySpuId(id));

    }
//
//    /**
//     * 根据sku ids查询sku
//     * @param ids
//     * @return
//     */
//    @GetMapping("sku/list/ids")
//    public ResponseEntity<List<Sku>> querySkusByIds(@RequestParam("ids") List<Long> ids) {
//        return ResponseEntity.ok(goodsService.querySkusByIds(ids));
//    }


    /**
     * 删除商品
     * @param spuId
     * @return
     */
    @DeleteMapping("spu/spuId/{spuId}")
    public ResponseEntity<Void> deleteGoodsBySpuId(@PathVariable("spuId") Long spuId) {
        goodsService.deleteGoodsBySpuId(spuId);
        return ResponseEntity.ok().build();
    }


    /**
     * 添加商品
     * @param spu
     * @return
     */
    @PostMapping("goods")
    public ResponseEntity<Void> addGoods(@RequestBody Spu spu) {
        goodsService.addGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 更新商品
     * @param spu
     * @return
     */
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody Spu spu) {
        goodsService.updateGoods(spu);
        //HTTP 204 No Content
        //成功状态响应代码指示请求已成功，但客户端无需离开其当前页面。
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("spu/saleable")
    public ResponseEntity<Void> handleSaleable(@RequestBody Spu spu) {
        goodsService.handleSaleable(spu);
        return ResponseEntity.ok().build();
    }

//    /**
//     * 根据spuId查询spu及skus
//     * @param spuId
//     * @return
//     */
//    @GetMapping("spu/{id}")
//    public ResponseEntity<Spu> querySpuBySpuId(@PathVariable("id") Long spuId) {
//        return ResponseEntity.ok(goodsService.querySpuBySpuId(spuId));
//    }
//
//    /**
//     * 减库存
//     * @param cartDtos
//     * @return
//     */
//    @PostMapping("stock/decrease")
//    public ResponseEntity<Void> decreaseStock(@RequestBody List<CartDto> cartDtos){
//        goodsService.decreaseStock(cartDtos);
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }



}
