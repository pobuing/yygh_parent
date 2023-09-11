package com.probuing.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.probuing.yygh.model.order.OrderInfo;
import com.probuing.yygh.model.order.PaymentInfo;

import java.util.Map;

/**
 * ClassName: PaymentInfoService
 * date: 2023/9/11 12:09
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
public interface PaymentInfoService extends IService<PaymentInfo> {
    void savePaymentInfo(OrderInfo orderInfo, Integer paymentType);

    void afterPaySuccess(Long orderId, Map<String, String> map);

}
