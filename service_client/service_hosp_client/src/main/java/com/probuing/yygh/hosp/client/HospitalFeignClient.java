package com.probuing.yygh.hosp.client;

import com.probuing.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * ClassName: HospitalFeignClient
 * date: 2023/8/25 17:33
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@FeignClient("service-hosp")
public interface HospitalFeignClient {
    @GetMapping("/api/hosp/hospital/inner/getScheduleOrderVo/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(@PathVariable("scheduleId") String scheduleId);

    @GetMapping("/api/hosp/hospital/getApiUrlByHoscode/{hoscode}")
    public String getApiUrlByHoscode(@PathVariable String hoscode);
}
