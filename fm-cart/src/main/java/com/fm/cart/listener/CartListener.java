package com.fm.cart.listener;

import com.fm.cart.service.CartService;
import com.fm.common.utils.JsonUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CartListener {

//    @Autowired
//    private CartService cartService;
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(name = "fm.cart.delete.queue", durable = "true"),
//            exchange = @Exchange(name = "fm.cart.exchange",
//                    type = ExchangeTypes.TOPIC,
//                    ignoreDeclarationExceptions = "true"),
//            key = {"cart.delete"}
//    ))
//    public void listenDelete(String params) {
//        Map<String, Object> map = JsonUtils.toMap(params, String.class, Object.class);
//        List<Object> ids = (List<Object>) map.get("skuIds");
//        Integer userId = (Integer) map.get("userId");
//        cartService.deleteCarts(ids, userId);
//    }
}
