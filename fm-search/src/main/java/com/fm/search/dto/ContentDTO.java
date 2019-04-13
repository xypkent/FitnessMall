package com.fm.search.dto;

import lombok.Data;

/**
 * 页面内容展示
 */
@Data
public class ContentDTO {
    private String clickLink = "###";
    private String imageLinks;
    private String title;
    private String price;

    public ContentDTO(String imageLinks) {
        this.imageLinks = imageLinks;
    }

    public ContentDTO(String imageLinks, String title, String price) {
        this.imageLinks = imageLinks;
        this.title = title;
        this.price = price;
    }
}