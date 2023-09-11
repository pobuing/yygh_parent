package com.probuing.yygh.hosp.service;

import com.probuing.yygh.model.hosp.Hospital;
import com.probuing.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * ClassName: HospitalService
 * date: 2023/8/20 19:06
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
public interface HospitalService {
    void save(Map<String, Object> paramMap);

    Hospital getByHoscode(String hoscode);

    Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    Hospital show(String id);

    void updateStatus(String id, Integer status);

    List<Hospital> findByHosname(String hosname);

    Map<String, Object> item(String hoscode);
}
