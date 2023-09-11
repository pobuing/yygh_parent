package com.probuing.yygh.order.service;

import java.util.Map;

/**
 * ClassName: WeixinService
 * date: 2023/9/11 11:58
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
public interface WeixinService {
    /**
     *
     * @param orderId 平台端的订单id
     * @return
     */
    Map createNative(Long orderId);

    Map<String, String> queryPayStatus(Long orderId);
}
