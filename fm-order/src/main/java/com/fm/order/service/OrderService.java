package com.fm.order.service;

import com.fm.auth.entity.UserInfo;
import com.fm.common.enums.ExceptionEnum;
import com.fm.common.exception.FmException;
import com.fm.common.utils.IdWorker;
import com.fm.common.utils.JsonUtils;
import com.fm.common.vo.PageResult;
import com.fm.item.pojo.Sku;
import com.fm.order.client.AddressClient;
import com.fm.order.client.GoodsClient;
import com.fm.order.dto.AddressDTO;
import com.fm.order.dto.OrderDto;
import com.fm.order.dto.OrderStatusEnum;
import com.fm.order.filter.LoginInterceptor;
import com.fm.order.mapper.OrderDetailMapper;
import com.fm.order.mapper.OrderMapper;
import com.fm.order.mapper.OrderStatusMapper;
import com.fm.order.pojo.Order;
import com.fm.order.pojo.OrderDetail;
import com.fm.order.pojo.OrderStatus;
import com.fm.order.utils.PayHelper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private PayHelper payHelper;

    @Autowired
    private PayLogService payLogService;

    @Autowired
    private AmqpTemplate amqpTemplate;


    @Transactional
    public Long createOrder(OrderDto orderDto) {
        //生成订单ID，采用自己的算法生成订单ID
        long orderId = idWorker.nextId();

        //填充order，订单中的用户信息数据从Token中获取，填充到order中
        Order order = new Order();
        order.setCreateTime(new Date());
        order.setOrderId(orderId);
        order.setPaymentType(orderDto.getPaymentType());
        order.setPostFee(0L);  //// TODO 调用物流信息，根据地址计算邮费

        //获取用户信息
        UserInfo user = LoginInterceptor.getLoginUser();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);  //卖家为留言

        //收货人地址信息，应该从数据库中物流信息中获取，这里使用的是假的数据
        AddressDTO addressDTO = AddressClient.findById(orderDto.getAddressId());
        if (addressDTO == null) {
            // 商品不存在，抛出异常
            throw new FmException(ExceptionEnum.RECEIVER_ADDRESS_NOT_FOUND);
        }
        order.setReceiver(addressDTO.getName());
        order.setReceiverAddress(addressDTO.getAddress());
        order.setReceiverCity(addressDTO.getCity());
        order.setReceiverDistrict(addressDTO.getDistrict());
        order.setReceiverMobile(addressDTO.getPhone());
        order.setReceiverZip(addressDTO.getZipCode());
        order.setReceiverState(addressDTO.getState());


        //付款金额相关，首先把orderDto转化成map，其中key为skuId,值为购物车中该sku的购买数量
        Map<Long, Integer> skuNumMap = orderDto.getCarts().stream()
                .collect(Collectors.toMap(c -> c.getSkuId(), c -> c.getNum()));

        //查询商品信息，根据skuIds批量查询sku详情
        List<Sku> skus = goodsClient.querySkusByIds(new ArrayList<>(skuNumMap.keySet()));

        if (CollectionUtils.isEmpty(skus)) {
            throw new FmException(ExceptionEnum.GOODS_NOT_FOUND);
        }

        Double totalPay = 0.0;

        //填充orderDetail
        ArrayList<OrderDetail> orderDetails = new ArrayList<>();

        //遍历skus
        for (Sku sku : skus) {
            Integer num = skuNumMap.get(sku.getId());
            totalPay += num * sku.getPrice();

            //填充orderDetail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setOwnSpec(sku.getOwnSpec());
            orderDetail.setSkuId(sku.getId());
            orderDetail.setTitle(sku.getTitle());
            orderDetail.setNum(num);
            orderDetail.setPrice(sku.getPrice().longValue());
            orderDetail.setImage(StringUtils.substringBefore(sku.getImages(), ","));

            orderDetails.add(orderDetail);
        }
        //实付金额：总金额 + 邮费 - 优惠
        order.setActualPay((totalPay.longValue() + order.getPostFee()));  //todo 还要减去优惠金额
        order.setTotalPay(totalPay.longValue());

        //保存order
        int count = orderMapper.insertSelective(order);
        if (count != 1){
            log.error("创建订单失败，orderId：{}",orderId);
            throw new FmException(ExceptionEnum.ORDER_CREATE_ERROR);
        }


        //保存details
        count = orderDetailMapper.insertList(orderDetails);
        if (count != orderDetails.size()){
            log.error("创建订单失败，orderId：{}",orderId);
            throw new FmException(ExceptionEnum.ORDER_CREATE_ERROR);
        }

        //填充orderStatus
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setStatus(OrderStatusEnum.INIT.value());
        orderStatus.setCreateTime(new Date());

        //保存orderStatus
        orderStatusMapper.insertSelective(orderStatus);

        //减库存,这里涉及乐观锁
        goodsClient.decreaseStock(orderDto.getCarts());


        //todo 删除购物车中已经下单的商品数据, 采用异步mq的方式通知购物车系统删除已购买的商品，传送商品ID和用户ID
//        HashMap<String, Object> map = new HashMap<>();
//        try {
//            map.put("skuIds", skuNumMap.keySet());
//            map.put("userId", user.getId());
//            amqpTemplate.convertAndSend("Fm.cart.exchange", "cart.delete", JsonUtils.toString(map));
//        } catch (Exception e) {
//            log.error("删除购物车消息发送异常，商品ID：{}", skuNumMap.keySet(), e);
//        }

        log.info("生成订单，订单编号：{}，用户id：{}", orderId, user.getId());
        return orderId;

    }

    public String generateUrl(Long orderId) {
        //根据订单ID查询订单
        Order order = queryById(orderId);
        //判断订单状态
        if (order.getOrderStatus().getStatus() != OrderStatusEnum.INIT.value()) {
            throw new FmException(ExceptionEnum.ORDER_STATUS_EXCEPTION);
        }
        OrderDetail detail = order.getOrderDetails().get(0);
        String desc = detail.getTitle();
        //todo 这里传入一份钱，用于测试使用，实际中使用订单中的实付金额
        String url = payHelper.createPayUrl(orderId, desc, /*order.getActualPay()*/1L);
        if (StringUtils.isBlank(url)) {
            throw new FmException(ExceptionEnum.CREATE_PAY_URL_ERROR);
        }

        //生成支付日志
//        payLogService.createPayLog(orderId, order.getActualPay());

        return url;

    }

    public Order queryById(Long orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new FmException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        //订单详情
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderId);
        List<OrderDetail> orderDetails = orderDetailMapper.select(orderDetail);
        if (CollectionUtils.isEmpty(orderDetails)) {
            throw new FmException(ExceptionEnum.ORDER_DETAIL_NOT_FOUND);
        }
        order.setOrderDetails(orderDetails);
        //订单状态
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);
        if (orderStatus == null) {
            throw new FmException(ExceptionEnum.ORDER_STATUS_NOT_FOUND);
        }
        order.setOrderStatus(orderStatus);
        return order;
    }

    @Transactional
    public void handleNotify(Map<String, String> msg) {
        payHelper.handleNotify(msg);
    }

    public PageResult<Order> queryOrderByPage(Integer page, Integer rows) {

        //开启分页
        PageHelper.startPage(page, rows);

        Example example = new Example(Order.class);

        //查询订单
        List<Order> orders = orderMapper.selectByExample(example);


        //查询订单详情
        for (Order order : orders) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(order.getOrderId());
            List<OrderDetail> orderDetailList = orderDetailMapper.select(orderDetail);

            order.setOrderDetails(orderDetailList);

            //查询订单状态
            OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(order.getOrderId());
            order.setOrderStatus(orderStatus);
        }

        PageInfo<Order> pageInfo = new PageInfo<>(orders);

        return new PageResult<>(pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getList());
    }
}
