package com.fm.page.web;

import com.fm.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class FmPageController {

    @Autowired
    private PageService pageService;

    /**
     * 查询详情页并记录用户足迹
     * @param spuId
     * @param model
     * @return
     */
    @GetMapping("item/{id}-{userId}.html")
    public String toItemPage(@PathVariable("id") Long spuId,@PathVariable Long userId, Model model) {
        Map<String, Object> attributes = pageService.loadModelAndFootPrint(spuId,userId);
        model.addAllAttributes(attributes);
        return "item";
    }
}















