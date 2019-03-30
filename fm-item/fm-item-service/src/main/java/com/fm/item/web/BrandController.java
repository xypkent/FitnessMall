package com.fm.item.web;

import com.fm.common.vo.PageResult;
import com.fm.item.service.BrandService;
import com.fm.item.pojo.Brand;
import com.fm.item.vo.BrandVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     * 分页查询品牌
     *
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @param key
     * @return
     */
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
            @RequestParam(value = "key", required = false) String key) {
        PageResult<Brand> result = brandService.queryBrandByPageAndSort(page, rows, sortBy, desc, key);
        if (result == null || result.getItems().size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 新增品牌
     *
     * @param brand
     * @param cids 品牌所在的分类ID（多个分类）
     * @return
     */
    @PostMapping  // 传入 "1,2,3"的字符串可以解析为列表
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        brandService.saveBrand(brand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * 更新品牌
     *
     * @param brandVo
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateBrand(BrandVo brandVo) {
        brandService.updateBrand(brandVo);
        return ResponseEntity.ok().build();
    }

    /**
     * 删除品牌
     *
     * @param bid
     * @return
     */
    @DeleteMapping("bid/{bid}")
    public ResponseEntity<Void> deleteBrand(@PathVariable("bid") Long bid) {
        brandService.deleteBrand(bid);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据分类ID查询品牌
     *
     * @param cid
     * @return
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("cid") Long cid) {
        return ResponseEntity.ok(brandService.queryBrandByCid(cid));
    }

    /**
     * 根据商品品牌ID查询品牌
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(brandService.queryBrandByBid(id));
    }

    /**
     * 根据ids查询品牌
     * @param ids
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Brand>> queryBrandsByIds(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(brandService.queryBrandByIds(ids));
    }

}
