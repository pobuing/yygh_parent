package com.probuing.yygh.user.client;

import com.probuing.yygh.model.user.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * ClassName: PatientFeighClient
 * date: 2023/8/25 16:58
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@FeignClient(value = "service-user")
@Repository
public interface PatientFeignClient {
    //获取就诊人
    @GetMapping("/api/user/patient/inner/get/{id}")
    Patient getPatientById(@PathVariable("id") Long id);

}
