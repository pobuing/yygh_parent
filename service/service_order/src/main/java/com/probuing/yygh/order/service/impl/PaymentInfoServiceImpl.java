package com.probuing.yygh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.probuing.yygh.enums.OrderStatusEnum;
import com.probuing.yygh.enums.PaymentStatusEnum;
import com.probuing.yygh.enums.PaymentTypeEnum;
import com.probuing.yygh.model.order.OrderInfo;
import com.probuing.yygh.model.order.PaymentInfo;
import com.probuing.yygh.order.mapper.PaymentMapper;
import com.probuing.yygh.order.service.OrderService;
import com.probuing.yygh.order.service.PaymentInfoService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * ClassName: PaymentInfoServiceImpl
 * date: 2023/9/11 12:11
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentMapper, PaymentInfo> implements PaymentInfoService {

    @Autowired
    private OrderService orderService;
    @Override
    public void savePaymentInfo(OrderInfo orderInfo, Integer paymentType) {
        //每一个订单最多一条支付记录
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderInfo.getId());
        queryWrapper.eq("payment_type", paymentType);
        PaymentInfo paymentInfo = baseMapper.selectOne(queryWrapper);
        if (paymentInfo != null) {
            return;
        }
        paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setTotalAmount(orderInfo.getAmount());
        String subject = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")
                + "|" + orderInfo.getHosname() + "|" + orderInfo.getDepname() + "|"
                + orderInfo.getTitle();
        paymentInfo.setSubject(subject);
        paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());
//        paymentInfo.setCallbackTime();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setUpdateTime(new Date());
        baseMapper.insert(paymentInfo);
    }

    @Override
    public void afterPaySuccess(Long orderId, Map<String, String> map) {
        OrderInfo orderInfo = orderService.getById(orderId);
        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());//设置订单状态为已支付
        orderService.updateById(orderInfo);
        //查询订单支付记录
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("out_trade_no", orderInfo.getOutTradeNo());
        queryWrapper.eq("payment_type", PaymentTypeEnum.WEIXIN.getStatus());
        PaymentInfo paymentInfo = baseMapper.selectOne(queryWrapper);
        paymentInfo.setUpdateTime(new Date());
        paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
        String transactionId = map.get("transaction_id");
        paymentInfo.setTradeNo(transactionId);
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(map.toString());
        baseMapper.updateById(paymentInfo);
    }
}
