package com.probuing.yygh.hosp;

import com.probuing.yygh.hosp.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ClassName: ScheduleTest
 * date: 2023/8/24 22:14
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@SpringBootTest
public class ScheduleTest {
    @Autowired
    private ScheduleService scheduleService;

    @Test
    public void testSchedule() {
        scheduleService.getBookingScheduleRule(1, 7, "10000", "");
    }
}
