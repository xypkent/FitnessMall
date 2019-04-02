package com.fm.page.client;

import com.fm.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(value = "item-service")
public interface GoodsClient extends GoodsApi {
}
