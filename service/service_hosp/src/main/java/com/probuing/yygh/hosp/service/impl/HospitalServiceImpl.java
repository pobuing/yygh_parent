package com.probuing.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.probuing.yygh.cmn.client.DictFeignClient;
import com.probuing.yygh.common.exp.YyghException;
import com.probuing.yygh.common.utils.MD5;
import com.probuing.yygh.enums.DictEnum;
import com.probuing.yygh.hosp.repository.HospitalRepository;
import com.probuing.yygh.hosp.service.HospitalService;
import com.probuing.yygh.hosp.service.HospitalSetService;
import com.probuing.yygh.model.hosp.BookingRule;
import com.probuing.yygh.model.hosp.Hospital;
import com.probuing.yygh.model.hosp.HospitalSet;
import com.probuing.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName: HospitalServiceImpl
 * date: 2023/8/20 19:07
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public void save(Map<String, Object> paramMap) {
        //签名校验

        String jsonString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(jsonString, Hospital.class);
        String logoData = hospital.getLogoData();
        String hoscode = hospital.getHoscode();
        String sign = (String) paramMap.get("sign");

        HospitalSet hospitalSet = hospitalSetService.findByHoccode(hoscode);
        if (hospitalSet == null) {
            throw new YyghException(20001, "该医院暂未开通权限");
        }
        String signKey = hospitalSet.getSignKey();
        String encrypt = MD5.encrypt(signKey);
        if (!sign.equalsIgnoreCase(encrypt)) {
            throw new YyghException(20001, "签名校验失败");
        }

        logoData = logoData.replaceAll(" ", "+");
        hospital.setLogoData(logoData);
        Hospital hospital_mg = hospitalRepository.findByHoscode(hoscode);

        if (hospital_mg == null) {
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setStatus(1);
        } else {
            hospital.setId(hospital_mg.getId());
            hospital.setUpdateTime(new Date());
            hospital.setCreateTime(hospital_mg.getCreateTime());
            hospital.setStatus(1);
        }

        hospitalRepository.save(hospital);
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.findByHoscode(hoscode);
        return hospital;
    }

    @Override
    public Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withIgnoreCase(true)
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Hospital> example = Example.of(hospital, exampleMatcher);
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Hospital> pageResult = hospitalRepository.findAll(example, pageable);
        //医院集合
        List<Hospital> hospitalList = pageResult.getContent();
        hospitalList.forEach(hosp -> {
            packHospital(hosp);

        });
        return pageResult;
    }

    private void packHospital(Hospital hosp) {
        String hostype = hosp.getHostype();
        String provinceCode = hosp.getProvinceCode();
        String cityCode = hosp.getCityCode();
        String districtCode = hosp.getDistrictCode();
        //医院服务调用数据字典服务
        String hostTypeString = dictFeignClient.getNameByValueAndDictCode(hostype, DictEnum.HOSTYPE.getDictCode());
        String provinceStr = dictFeignClient.getNameByValueAndDictCode(provinceCode);
        String cityStr = dictFeignClient.getNameByValueAndDictCode(cityCode);
        String districtStr = dictFeignClient.getNameByValueAndDictCode(districtCode);
        String fullAddress = provinceStr + cityStr + districtStr + hosp.getAddress();
        hosp.getParam().put("hostypeString", hostTypeString);
        hosp.getParam().put("fullAddress", fullAddress);
    }

    @Override
    public Hospital show(String id) {
        Hospital hospital = hospitalRepository.findById(id).get();
        this.packHospital(hospital);
        return hospital;
    }

    @Override
    public void updateStatus(String id, Integer status) {
        //查询数据
        Hospital hospital = hospitalRepository.findById(id).get();
        hospital.setStatus(status);
        hospitalRepository.save(hospital);

    }

    @Override
    public List<Hospital> findByHosname(String hosname) {
        List<Hospital> list = hospitalRepository.findByHosnameLike(hosname);
        list.forEach(hospital -> packHospital(hospital));
        return list;
    }

    @Override
    public Map<String, Object> item(String hoscode) {
        Hospital hospital = hospitalRepository.findByHoscode(hoscode);
        packHospital(hospital);
        BookingRule bookingRule = hospital.getBookingRule();
        Map<String, Object> map = new HashMap<>();
        map.put("hospital", hospital);
        map.put("bookingRule", bookingRule);
        return map;
    }


}
