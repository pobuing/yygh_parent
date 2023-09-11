package com.probuing.yygh.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.probuing.yygh.common.result.R;
import com.probuing.yygh.common.utils.JwtHelper;
import com.probuing.yygh.enums.OrderStatusEnum;
import com.probuing.yygh.model.order.OrderInfo;
import com.probuing.yygh.order.service.OrderService;
import com.probuing.yygh.vo.order.OrderCountQueryVo;
import com.probuing.yygh.vo.order.OrderQueryVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * ClassName: OrderController
 * date: 2023/8/25 16:45
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderApiController {
    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "创建订单")
    @PostMapping("auth/submitOrder/{scheduleId}/{patientId}")
    public R submitOrder(
            @ApiParam(name = "scheduleId", value = "排班id", required = true)
            @PathVariable String scheduleId,
            @ApiParam(name = "patientId", value = "就诊人id", required = true)
            @PathVariable Long patientId) {
        Long orderId = orderService.saveOrder(scheduleId, patientId);
        return R.ok().data("orderId", orderId);
    }

    @GetMapping("auth/getOrder/{orderId}")
    public R getOrder(@PathVariable Long orderId) {
        OrderInfo orderInfo = orderService.getOrderInfo(orderId);
        return R.ok().data("orderInfo", orderInfo);
    }

    @GetMapping("auth/{page}/{limit}")
    public R list(@PathVariable Long page,
                  @PathVariable Long limit,
                  OrderQueryVo orderQueryVo, HttpServletRequest request) {
        Long userId = JwtHelper.getUserId(request.getHeader("token"));
        orderQueryVo.setUserId(userId);
        Page<OrderInfo> orderInfoPage = new Page<>(page, limit);
        IPage<OrderInfo> pageModel = orderService.selectPage(orderInfoPage, orderQueryVo);
        return R.ok().data("pageModel", pageModel);

    }

    @ApiOperation(value = "获取订单统计数据")
    @PostMapping("inner/getCountMap")
    public Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo) {
        return orderService.getCountMap(orderCountQueryVo);
    }

    @ApiOperation(value = "获取订单状态")
    @GetMapping("auth/getStatusList")
    public R getStatusList() {
        List<Map<String, Object>> statusList = OrderStatusEnum.getStatusList();
        return R.ok().data("statusList", statusList);
    }

}
