package com.probuing.yygh.hosp.service;

import com.probuing.yygh.model.hosp.Schedule;
import com.probuing.yygh.vo.hosp.ScheduleOrderVo;
import com.probuing.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {

    //scheduleId 平台端排班的id（mg）
    ScheduleOrderVo getScheduleOrderVo(String scheduleId);


    Schedule getById(String id);

    /**
     * 查看排班详情
     * @param hoscode
     * @param depcode
     * @param workDate
     * @return
     */
    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);

    /**
     *
     * @param page
     * @param limit
     * @param hoscode
     * @param depcode
     * @return
     */
    Map<String, Object> getScheduleRule(long page, long limit, String hoscode, String depcode);


    public void save(Map<String, Object> paramMap);

    Page<Schedule> selectPage(int pageNum, int pageSize, ScheduleQueryVo scheduleQueryVo);

    void remove(String hoscode, String hosScheduleId);

    Map<String, Object> getBookingSchedule(Integer page, Integer limit, String hoscode, String depcode);

    void getBookingScheduleRule(int i, int i1, String number, String s);

}
