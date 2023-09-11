package com.probuing.yygh.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.probuing.yygh.model.order.OrderInfo;
import com.probuing.yygh.vo.order.OrderCountQueryVo;
import com.probuing.yygh.vo.order.OrderCountVo;
import org.springframework.stereotype.Repository;

import java.util.List;

//创建mapper
@Repository
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
    //统计每天平台预约数据
    List<OrderCountVo> selectOrderCount(OrderCountQueryVo orderCountQueryVo);
}