package com.probuing.yygh.user.controller;

import com.probuing.yygh.common.result.R;
import com.probuing.yygh.common.utils.AuthContextHolder;
import com.probuing.yygh.model.user.Patient;
import com.probuing.yygh.user.service.PatientService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * ClassName: PatientController
 * date: 2023/8/24 17:43
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
//就诊人管理接口
@RestController
@RequestMapping("/api/user/patient")
public class PatientController {
    @Autowired
    private PatientService patientService;

    @GetMapping("auth/findAll")
    public R findAll(HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        List<Patient> patientList = patientService.findAllUserId(userId);
        return R.ok().data("list", patientList);
    }

    //添加就诊人
    @PostMapping("auth/save")
    public R savePatient(@RequestBody Patient patient, HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        patient.setUserId(userId);
        patientService.save(patient);
        return R.ok();
    }

    //根据id获取就诊人信息
    @GetMapping("auth/get/{id}")
    public R getPatient(@PathVariable Long id) {
        Patient patient = patientService.getPatientId(id);
        return R.ok().data("patient", patient);
    }

    //修改就诊人
    @PostMapping("auth/update")
    public R updatePatient(@RequestBody Patient patient) {
        patientService.updateById(patient);
        return R.ok();
    }

    //删除就诊人
    @DeleteMapping("auth/remove/{id}")
    public R removePatient(@PathVariable Long id) {
        patientService.removeById(id);
        return R.ok();
    }

    @ApiOperation(value = "获取就诊人")
    @GetMapping("inner/get/{id}")
    public Patient getPatientById(
            @ApiParam(name = "id", value = "就诊人id", required = true)
            @PathVariable("id") Long id) {
        return patientService.getById(id);
    }
}
