package com.fm.order.web;

import com.fm.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class PayNotifyController {

    @Autowired
    private OrderService orderService;

    @PostMapping(value = "/wxpay/notify",produces = "application/xml")
    public ResponseEntity<String> payNotify(@RequestBody Map<String, String> msg) {
        //处理回调结果
        orderService.handleNotify(msg);
        // 没有异常，则返回成功
        //方式一
        String result = "<xml>\n" +
                "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                "</xml>";
        //方式二
//        Map<String,String> msg = new HashMap<>();
//        msg.put("return_code","SUCCESS");
//        msg.put("return_msg","OK");

        log.info("[支付回调] 接收微信支付回调，结果：{}",result);

        return ResponseEntity.ok(result);

    }
}
