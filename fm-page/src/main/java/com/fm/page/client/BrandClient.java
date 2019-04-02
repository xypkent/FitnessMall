package com.fm.page.client;

import com.fm.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient("item-service")
public interface BrandClient extends BrandApi {
}
