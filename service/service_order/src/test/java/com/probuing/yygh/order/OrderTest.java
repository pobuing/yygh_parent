package com.probuing.yygh.order;

import com.probuing.yygh.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ClassName: OrderTest
 * date: 2023/8/25 17:49
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@SpringBootTest
public class OrderTest {
    @Autowired
    OrderService orderService;
    @Test
    public void test1() {
        String sid = "636a12ceb4fe9e2fa956201e";
        Long pid = 8L;
        orderService.saveOrder(sid, pid);
    }
}
