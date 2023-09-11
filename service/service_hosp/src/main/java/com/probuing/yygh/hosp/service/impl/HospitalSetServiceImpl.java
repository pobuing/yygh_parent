package com.probuing.yygh.hosp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.probuing.yygh.hosp.mapper.HospitalSetMapper;
import com.probuing.yygh.hosp.service.HospitalSetService;
import com.probuing.yygh.model.hosp.HospitalSet;
import org.springframework.stereotype.Service;

/**
 * ClassName: HospitalSetServiceImpl
 * date: 2023/8/10 18:07
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {

    @Override
    public HospitalSet findByHoccode(String hoscode) {
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode", hoscode);
        HospitalSet hospitalSet = getOne(wrapper);
        return hospitalSet;
    }
}
