package com.probuing.yygh.hosp.dept;

import com.probuing.yygh.hosp.repository.ScheduleRepository;
import com.probuing.yygh.hosp.service.ScheduleService;
import com.probuing.yygh.model.hosp.Schedule;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * ClassName: DeptTest
 * date: 2023/8/22 17:18
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@SpringBootTest
public class DeptTest {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private ScheduleService scheduleService;
    String hoscode = "10000";
    String depcode = "200040878";
    @Test
    public void testSchedule() {

        //查询hoscode下所有的排班
        List<Schedule> list = scheduleRepository.findByHoscodeAndDepcode(hoscode, depcode);
        //统计这些排班都来自于哪些日期
        Map<Date, List<Schedule>> collect = list.stream().collect(Collectors.groupingBy(Schedule::getWorkDate));
        System.out.println(collect.entrySet().size());

        collect.entrySet().forEach(entry -> {
            Date key = entry.getKey();
            List<Schedule> value = entry.getValue();
            System.out.println("entry.getKey() = " + key);
            System.out.println("entry.getValue() = " + value);
            AtomicReference<Integer> reservedNumberSum = new AtomicReference<>(0);
            AtomicReference<Integer> availableNumberSum = new AtomicReference<>(0);
            value.forEach(schedule -> {
                Integer reservedNumber = schedule.getReservedNumber();
                Integer availableNumber = schedule.getAvailableNumber();
                reservedNumberSum.updateAndGet(v -> v + reservedNumber);
                availableNumberSum.updateAndGet(v -> v + availableNumber);
            });
            System.out.println("reservedNumberSum = " + reservedNumberSum);
            System.out.println("availableNumberSum = " + availableNumberSum);

        });
    }
    @Test
    public void testWeekOfDay() {
        Date date = new Date();
        DateTime dateTime = new DateTime(date);
        System.out.println("dateTime.getDayOfWeek() = " + dateTime.getDayOfWeek());
    }

    @Test
    public void testMongo() {
        scheduleService.getScheduleRule(0, 0, hoscode, depcode);
    }
}
