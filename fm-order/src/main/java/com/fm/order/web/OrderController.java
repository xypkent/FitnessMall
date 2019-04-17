package com.fm.order.web;

import com.fm.common.vo.PageResult;
import com.fm.order.dto.OrderDto;
import com.fm.order.pojo.Order;
import com.fm.order.service.OrderService;
import com.fm.order.service.PayLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PayLogService payLogService;

    /**
     * 创建订单
     *
     * @param orderDto
     * @return
     */
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody @Valid OrderDto orderDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(orderDto));
    }

    /**
     * 根据订单ID查询订单详情
     *
     * @param orderId
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Order> queryOrderById(@PathVariable("id") Long orderId) {
        return ResponseEntity.ok(orderService.queryById(orderId));
    }

    /**
     * 生成微信支付链接
     *
     * @param orderId
     * @return
     */
    @GetMapping("url/{id}")
    public ResponseEntity<String> generateUrl(@PathVariable("id") Long orderId) {
        return ResponseEntity.ok(orderService.generateUrl(orderId));
    }

    /**
     * 查询订单支付状态
     *
     * @param orderId
     * @return
     */
    @GetMapping("state/{id}")
    public ResponseEntity<Integer> queryOrderStateByOrderId(@PathVariable("id") Long orderId) {
        return ResponseEntity.ok(payLogService.queryOrderStateByOrderId(orderId).getValue());
    }

    /**
     * 分页查询所有订单
     *
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<PageResult<Order>> queryOrderByPage(@RequestParam("page") Integer page,
                                                              @RequestParam("rows") Integer rows) {
        return ResponseEntity.ok(orderService.queryOrderByPage(page, rows));
    }



}
