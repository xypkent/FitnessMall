package com.fm.item.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 视图对象，用于展示层，它的作用是把某个指定页面（或组件）的所有数据封装起来。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandVo {

    private Long id;
    private String name;
    private String image;
    private List<Long> cids;
    private Character letter;
}
