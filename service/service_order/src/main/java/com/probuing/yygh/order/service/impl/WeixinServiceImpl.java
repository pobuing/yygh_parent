package com.probuing.yygh.order.service.impl;

import com.github.wxpay.sdk.WXPayUtil;
import com.probuing.yygh.enums.PaymentTypeEnum;
import com.probuing.yygh.model.order.OrderInfo;
import com.probuing.yygh.order.service.OrderService;
import com.probuing.yygh.order.service.PaymentInfoService;
import com.probuing.yygh.order.service.WeixinService;
import com.probuing.yygh.order.util.ConstantPropertiesUtils;
import com.probuing.yygh.order.util.HttpClient;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: WeixinServiceImpl
 * date: 2023/9/11 11:59
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@Service
public class WeixinServiceImpl implements WeixinService {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    PaymentInfoService paymentInfoService;

    @Autowired
    OrderService orderService;

    @Override
    public Map createNative(Long orderId) {
        //判断redis中是否存在支付链接 存在---return 不存在---创建
        Map map = (Map) redisTemplate.boundValueOps(orderId).get();
        if (map != null) {
            return map;
        }
        OrderInfo orderInfo = orderService.getById(orderId);
        //创建支付记录
        paymentInfoService.savePaymentInfo(orderInfo, PaymentTypeEnum.WEIXIN.getStatus());
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", ConstantPropertiesUtils.APPID);
        paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("sign", ConstantPropertiesUtils.PARTNERKEY);
        Date reserveDate = orderInfo.getReserveDate();
        String reserveDateString = new DateTime(reserveDate).toString("yyyy/MM/dd");
        String body = reserveDateString + "就诊" + orderInfo.getDepname();
        paramMap.put("body", body);
        paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
        paramMap.put("total_fee", "1");//为了测试
        paramMap.put("spbill_create_ip", "127.0.0.1");
        paramMap.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");
        paramMap.put("trade_type", "NATIVE");
        try {
            String s = WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY);
            //调用微信的统一下单接口
            //2、HTTPClient来根据URL访问第三方接口并且传递参数
            String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            httpClient.setXmlParam(s);
            httpClient.post();
            String content = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            String code_url = resultMap.get("code_url");
            HashMap<String, Object> result = new HashMap<>();
            result.put("orderId", orderId);
            result.put("totalFee", orderInfo.getAmount());
            result.put("resultCode", resultMap.get("result_code"));
            result.put("codeUrl", resultMap.get("code_url"));
            if (null != resultMap.get("result_code")) {
                //微信支付二维码2小时过期，可采取2小时未支付取消订单
                redisTemplate.opsForValue().set(orderId.toString(), result, 1000, TimeUnit.MINUTES);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Map<String, String> queryPayStatus(Long orderId) {
        OrderInfo orderInfo = orderService.getById(orderId);
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("appid", ConstantPropertiesUtils.APPID);
        paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
        paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        try {
            String s = WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY);
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            httpClient.setXmlParam(s);
            httpClient.post();
            String content = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            return resultMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
