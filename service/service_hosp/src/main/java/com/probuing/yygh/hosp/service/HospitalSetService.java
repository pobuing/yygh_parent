package com.probuing.yygh.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.probuing.yygh.model.hosp.HospitalSet;

/**
 * ClassName: HospitalSetService
 * date: 2023/8/10 18:07
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
public interface HospitalSetService extends IService<HospitalSet> {
    HospitalSet findByHoccode(String hoscode);
}
