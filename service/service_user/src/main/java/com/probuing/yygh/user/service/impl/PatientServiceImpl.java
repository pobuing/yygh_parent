package com.probuing.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.probuing.yygh.cmn.client.DictFeignClient;
import com.probuing.yygh.model.user.Patient;
import com.probuing.yygh.user.mapper.PatientMapper;
import com.probuing.yygh.user.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: PatientServiceImpl
 * date: 2023/8/24 17:32
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {


    @Autowired
    DictFeignClient dictFeignClient;

    @Override
    public List<Patient> findAllUserId(Long userId) {
        QueryWrapper<Patient> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<Patient> patientList = baseMapper.selectList(queryWrapper);
        patientList.forEach(patient -> {
            packPatient(patient);
        });
        return patientList;
    }

    @Override
    public Patient getPatientId(Long id) {
        Patient patient = baseMapper.selectById(id);
        packPatient(patient);
        return patient;
    }

    private void packPatient(Patient patient) {
        String provinceCode = patient.getProvinceCode();
        String cityCode = patient.getCityCode();
        String districtCode = patient.getDistrictCode();
        String certificatesType = patient.getCertificatesType();
        String contactsCertificatesType = patient.getContactsCertificatesType();
        String provinceString = dictFeignClient.getNameByValueAndDictCode(provinceCode);
        String cityString = dictFeignClient.getNameByValueAndDictCode(cityCode);
        String districtString = dictFeignClient.getNameByValueAndDictCode(districtCode);
        String certificatesTypeString = dictFeignClient.getNameByValueAndDictCode(certificatesType);
//        String contactsCertificatesTypeString = dictFeignClient.getNameByValueAndDictCode(contactsCertificatesType);
        patient.getParam().put("provinceString", provinceString);
        patient.getParam().put("cityString", cityString);
        patient.getParam().put("districtString", districtString);
        patient.getParam().put("certificatesTypeString", certificatesTypeString);
        patient.getParam().put("contactsCertificatesTypeString", contactsCertificatesType);

    }
}
