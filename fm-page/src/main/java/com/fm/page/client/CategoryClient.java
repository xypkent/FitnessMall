package com.fm.page.client;

import com.fm.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient("item-service")
public interface CategoryClient extends CategoryApi {
}
