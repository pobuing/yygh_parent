package com.probuing.yygh.order.controller;

import com.probuing.yygh.common.result.R;
import com.probuing.yygh.order.service.PaymentInfoService;
import com.probuing.yygh.order.service.WeixinService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ClassName: WeiXinController
 * date: 2023/9/11 11:56
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@RestController
@RequestMapping("/api/order/weixin")
public class WeiXinController {
    @Autowired
    private WeixinService weixinPayService;

    @Autowired
    private PaymentInfoService paymentInfoService;

    /**
     * 下单 生成二维码
     */
    @GetMapping("/createNative/{orderId}")
    public R createNative(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @PathVariable("orderId") Long orderId) {
        Map map = weixinPayService.createNative(orderId);
        return R.ok().data(map);
    }

    @ApiOperation(value = "查询支付状态")
    @GetMapping("/queryPayStatus/{orderId}")
    public R queryPayStatus(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @PathVariable("orderId") Long orderId) {
        Map<String, String> map = weixinPayService.queryPayStatus(orderId);
        if (map == null) {
            return R.error().message("支付出错");
        }
        String trade_state = map.get("trade_state");
        if (StringUtils.isEmpty(trade_state)) {
            return R.error().message("支付出错");
        }
        if (trade_state.equalsIgnoreCase("SUCCESS")) {
            paymentInfoService.afterPaySuccess(orderId, map);
            return R.ok().message("支付成功");
        }
        return R.ok().message("支付中");
    }
}
