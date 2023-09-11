package com.probuing.yygh.service.sms.controller;

import com.probuing.yygh.common.result.R;
import com.probuing.yygh.service.sms.service.SmsService;
import com.probuing.yygh.service.sms.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * ClassName: SmsController
 * date: 2023/8/23 20:22
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@RestController
@RequestMapping("/api/msm")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("send/{phone}")
    public R send(@PathVariable String phone) {
        if (StringUtils.isEmpty(phone)) {
            return R.error().message("手机号不能为空");
        }
        //判断code是否过期
        String code_redis = redisTemplate.boundValueOps(phone).get();
        if (!StringUtils.isEmpty(code_redis)) {
            return R.ok().message("验证码未过期");
        }


        //创建code
        String code = RandomUtil.getSixBitRandom();
        boolean send = smsService.send(phone, code);
        if (send) {
            redisTemplate.boundValueOps(phone).set(code, 5L, TimeUnit.MINUTES);
            return R.ok().message("发送成功");
        } else {
            return R.error().message("发送成功");
        }
    }
}
