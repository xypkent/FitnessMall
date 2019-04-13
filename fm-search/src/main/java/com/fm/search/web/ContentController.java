package com.fm.search.web;

import com.fm.search.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 提供页面定制化内容
 */
@RestController
@RequestMapping("content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @GetMapping("all")
    public ResponseEntity<Map<String,Object>> queryIndexAll() {
        Map<String,Object> all = contentService.queryIndexContent();
        return ResponseEntity.ok(all);
    }

}
