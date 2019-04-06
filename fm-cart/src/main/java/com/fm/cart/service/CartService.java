package com.fm.cart.service;

import com.fm.auth.entity.UserInfo;
import com.fm.cart.filter.LoginInterceptor;
import com.fm.cart.pojo.Cart;
import com.fm.common.enums.ExceptionEnum;
import com.fm.common.exception.FmException;
import com.fm.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartService {

    private static final String KEY_PREFIX = "fm:cart:uid:";

    private static final Integer NUM_LIMIT = 99;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 添加到购物车
     * @param cart
     */
    public void addCart(Cart cart) {
        Integer num = cart.getNum();
        //获取用户
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        String key = KEY_PREFIX + userInfo.getId();
        //hash操作对象
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);
        //购物车限量99条
        List<Object> carts = redisTemplate.boundHashOps(key).values();
        if (carts.size() == NUM_LIMIT){
            throw new FmException(ExceptionEnum.CART_IS_FULL);
        }
        //判断当前商品在购物车中是否存在
        String hashKey = cart.getSkuId().toString();
        if (hashOps.hasKey(hashKey)){
            //存在，查询修改数量
            String json = hashOps.get(hashKey).toString();
            //反序列化
            cart = JsonUtils.toBean(json, Cart.class);
            //修改数量
            cart.setNum(num + cart.getNum());
        }
        //如果不存在，直接写入
        hashOps.put(hashKey,JsonUtils.toString(cart));
    }

    /**
     * 查询当前用户的购物车列表
     * @return
     */
    public List<Cart> queryCartList() {
        //获取登录用户
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        //判断是否存在购物车
        String key = KEY_PREFIX + userInfo.getId();
        //不存在
        if (!redisTemplate.hasKey(key)) {
            throw new FmException(ExceptionEnum.CART_NOT_FOUND);
        }
        //查询购物车
        return redisTemplate.boundHashOps(key).values().stream()
                .map(o -> JsonUtils.toBean(o.toString(),Cart.class)).collect(Collectors.toList());
    }

    /**
     * 修改购物车商品数量
     * @param skuId
     * @param num
     */
    public void updateNum(Long skuId, Integer num) {
        //获取登录用户
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        //判断是否存在购物车
        String key = KEY_PREFIX + userInfo.getId();
        //hash操作对象
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);
        //判断是否存在
        if (!hashOps.hasKey(skuId.toString())){
            //不存在
            throw new FmException(ExceptionEnum.CART_NOT_FOUND);
        }
        //存在，查询出来修改
        String json = hashOps.get(skuId.toString()).toString();
        Cart cart = JsonUtils.toBean(json, Cart.class);
        cart.setNum(num);
        //写到redis
        hashOps.put(skuId.toString(),JsonUtils.toString(cart));
    }

    /**
     * 删除购物车中商品
     * @param skuId
     */
    public void deleteCart(Long skuId) {
        //获取登录用户
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        String key = KEY_PREFIX + userInfo.getId();
        this.redisTemplate.opsForHash().delete(key,skuId.toString());
    }
}
