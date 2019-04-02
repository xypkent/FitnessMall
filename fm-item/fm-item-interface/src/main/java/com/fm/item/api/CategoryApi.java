package com.fm.item.api;


import com.fm.item.pojo.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("category")
public interface CategoryApi {

    @GetMapping("list/ids")
    List<Category> queryByIds(@RequestParam("ids") List<Long> ids);

    @GetMapping("all/level")
    List<Category> queryAllByCid3(@RequestParam("id") Long id);
}
