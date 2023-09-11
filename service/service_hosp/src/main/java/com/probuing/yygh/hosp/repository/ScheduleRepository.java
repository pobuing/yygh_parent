package com.probuing.yygh.hosp.repository;

import com.probuing.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * ClassName: ScheduleRepository
 * date: 2023/8/20 21:36
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@Repository
public interface ScheduleRepository extends MongoRepository<Schedule, String> {
    /**
     * 根据医院编号和排班ID查询排班
     *
     * @param hoscode
     * @param hosScheduleId
     * @return
     */
    Schedule findByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    List<Schedule> findByHoscodeAndDepcode(String hoscode, String depcode);

    List<Schedule> findByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date workDate);
}
