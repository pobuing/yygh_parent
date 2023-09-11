package com.probuing.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.probuing.yygh.model.user.Patient;

import java.util.List;

/**
 * ClassName: PatientService
 * date: 2023/8/24 17:31
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
public interface PatientService extends IService<Patient> {
    //获取就诊人列表
    List<Patient> findAllUserId(Long userId);

    //根据id获取就诊人信息
    Patient getPatientId(Long id);
}
