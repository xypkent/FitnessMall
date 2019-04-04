package com.fm.sms.listener;

import com.fm.sms.properties.SmsProperties;
import com.fm.sms.utils.SmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

//MQ
@Slf4j
@Component
@EnableConfigurationProperties(value = SmsProperties.class)
public class SmsListener {

    @Autowired
    private SmsProperties props;

    @Autowired
    private SmsUtil smsUtil;

    /**
     * 发送短信验证码
     * @param msg
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "fm.sms.verify.queue"),
            exchange = @Exchange(name = "fm.sms.exchange", type = ExchangeTypes.TOPIC),
            key = "sms.verify.code"
    ))
    public void listenVerifyCode(Map<String, Object> msg) {
        if (CollectionUtils.isEmpty(msg)) {
            return;
        }
        String phone = (String) msg.remove("phone");
        if (StringUtils.isBlank(phone)) {
            return;
        }
        smsUtil.sendSms(props.getSignName(), props.getVerifyCodeTemplate(), phone, msg);

        //发送短信日志
        log.info("[短信服务]，发送短信验证码，手机号：{}",phone);
    }
}
