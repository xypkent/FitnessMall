package com.fm.user.service;

import com.fm.common.enums.ExceptionEnum;
import com.fm.common.exception.FmException;
import com.fm.common.utils.NumberUtils;
import com.fm.user.mapper.UserMapper;
import com.fm.user.pojo.User;
import com.fm.user.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "user:verify:code:";


    public Boolean checkData(String data, Integer type) {
        User user = new User();
        //判断校验数据的类型
        //1，用户名；2，手机；
        switch (type) {
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                throw new FmException(ExceptionEnum.INVALID_PARAM);
        }
        return (userMapper.selectCount(user) == 0);
    }

    //todo 信息可保存在配置文件中
    public void sendVerifyCode(String phone) {

        //随机生成6位数字验证码
        String code = NumberUtils.generateCode(6);

        String key = KEY_PREFIX + phone;

        //把验证码放入Redis中，并设置有效期为5min
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);

        //向mq中发送消息
        Map<String,String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("code", code);
        amqpTemplate.convertAndSend("fm.sms.exchange", "sms.verify.code", map);
    }

    public void register(User user, String code) {
        user.setId(null);
        user.setCreated(new Date());
        String key = KEY_PREFIX + user.getPhone();

        String cacheCode = redisTemplate.opsForValue().get(key);
        //校验验证码
        if (!StringUtils.equals(code, cacheCode)) {
            //验证码不匹配
            throw new FmException(ExceptionEnum.VERIFY_CODE_NOT_MATCHING);
        }

        //生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        //生成密码
        String md5Pwd = CodecUtils.md5Hex(user.getPassword(), user.getSalt());
        user.setPassword(md5Pwd);

        //保存到数据库
        int count = userMapper.insert(user);
        if (count != 1) {
            throw new FmException(ExceptionEnum.INVALID_PARAM);
        }
        //把验证码从Redis中删除
        redisTemplate.delete(key);
    }

    public User queryUser(String username, String password) {
        User record = new User();
        record.setUsername(username);

        //首先根据用户名查询用户
        // （数据库中对用户名进行了索引，可以加快速度）
        User user = userMapper.selectOne(record);

        if (user == null) {
            throw new FmException(ExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
        }

        //检验密码是否正确
        if (!StringUtils.equals(CodecUtils.md5Hex(password, user.getSalt()), user.getPassword())) {
            //密码不正确
            throw new FmException(ExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
        }

        return user;
    }
}
