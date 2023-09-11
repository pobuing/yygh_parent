package com.probuing.yygh.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.probuing.yygh.model.order.OrderInfo;
import com.probuing.yygh.vo.order.OrderCountQueryVo;
import com.probuing.yygh.vo.order.OrderQueryVo;

import java.util.Map;

/**
 * ClassName: OrderService
 * date: 2023/8/25 16:43
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
public interface OrderService extends IService<OrderInfo> {

    Long saveOrder(String scheduleId, Long patientId);

    OrderInfo getOrderInfo(Long id);

    IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo);

    void patientTips();

    /**
     * 订单统计
     */
    Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo);
}
