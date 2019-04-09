package com.fm.order.service;

import com.fm.order.enums.OrderStatusEnum;
import com.fm.order.enums.PayStateEnum;
import com.fm.order.filter.LoginInterceptor;
import com.fm.order.mapper.OrderStatusMapper;
import com.fm.order.mapper.PayLogMapper;
import com.fm.order.pojo.OrderStatus;
import com.fm.order.pojo.PayLog;
import com.fm.order.utils.PayHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class PayLogService {

    @Autowired
    private PayLogMapper payLogMapper;

    @Autowired
    private OrderStatusMapper  statusMapper;

    @Autowired
    private PayHelper payHelper;

    public void createPayLog(Long orderId, Long actualPay) {
        //创建支付对象
        PayLog payLog = new PayLog();
        payLog.setStatus(PayStateEnum.NOT_PAY.getValue());
        payLog.setPayType(1);
        payLog.setOrderId(orderId);
        payLog.setTotalFee(actualPay);
        payLog.setCreateTime(new Date());
        //获取用户信息
        payLog.setUserId(LoginInterceptor.getLoginUser().getId());

        payLogMapper.insertSelective(payLog);
    }

    @Transactional
    public PayStateEnum queryOrderStateByOrderId(Long orderId) {

        //查询订单状态
        OrderStatus orderStatus = statusMapper.selectByPrimaryKey(orderId);
        Integer status = orderStatus.getStatus();
        //判断是否支付
        if (status != OrderStatusEnum.INIT.value()) {
            //如果已支付，真的是支付
            return PayStateEnum.SUCCESS;
        }

        //如果未支付，单其实不一定是未支付，必须去微信查询支付状态
        return payHelper.queryPayState(orderId);



        //结合日志表的代码：------------------------------------------------
/*        //优先去支付日志表中查询信息
        PayLog payLog = payLogMapper.selectByPrimaryKey(orderId);
        if (payLog == null || PayStateEnum.NOT_PAY.getValue() == payLog.getStatus()) {
            //未支付，调用微信接口查询订单支付状态
            return payHelper.queryPayState(orderId);
        }

        if (PayStateEnum.SUCCESS.getValue() == payLog.getStatus()) {
            //支付成功，返回1
            return PayStateEnum.SUCCESS;
        }

        //如果是其他状态，返回失败
        return PayStateEnum.FAIL;*/
    }
}
