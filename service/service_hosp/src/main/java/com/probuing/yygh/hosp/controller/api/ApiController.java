package com.probuing.yygh.hosp.controller.api;

import com.probuing.yygh.common.result.Result;
import com.probuing.yygh.common.utils.HttpRequestHelper;
import com.probuing.yygh.hosp.service.DepartmentService;
import com.probuing.yygh.hosp.service.HospitalService;
import com.probuing.yygh.hosp.service.ScheduleService;
import com.probuing.yygh.model.hosp.Department;
import com.probuing.yygh.model.hosp.Hospital;
import com.probuing.yygh.model.hosp.Schedule;
import com.probuing.yygh.vo.hosp.DepartmentQueryVo;
import com.probuing.yygh.vo.hosp.ScheduleQueryVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * ClassName: ApiController
 * date: 2023/8/20 18:54
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@Api("医院端调用的接口")
@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    @PostMapping("saveHospital")
    public Result saveHospital(HttpServletRequest request) {
       /* Map<String, String[]> parameterMap = request.getParameterMap();
        HashMap<String, String> paramMap = new HashMap<>();
        parameterMap.keySet().forEach(key -> {
            String[] values = parameterMap.get(key);
            paramMap.put(key, values[0]);
        });*/
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());

        hospitalService.save(paramMap);
        return Result.ok();
    }

    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String hoscode = (String) paramMap.get("hoscode");
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);
    }

    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        departmentService.save(paramMap);
        return Result.ok();
    }

    @PostMapping("department/list")
    public Result departmentList(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String hoscode = (String) paramMap.get("hoscode");
        String page = (String) paramMap.get("page");
        String limit = (String) paramMap.get("limit");
        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);

        Page<Department> pageResult = departmentService.selectPate(Integer.parseInt(page), Integer.parseInt(limit), departmentQueryVo);
        return Result.ok(pageResult);
    }

    @PostMapping("department/remove")
    public Result remove(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        departmentService.remove(hoscode, depcode);
        return Result.ok();
    }

    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        scheduleService.save(paramMap);
        return Result.ok();
    }

    @PostMapping("schedule/list")
    public Result scheduleList(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        String pageNum = (String) paramMap.get("page");
        String pageSize = (String) paramMap.get("limit");
        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);

        Page<Schedule> page = scheduleService.selectPage(Integer.parseInt(pageNum), Integer.parseInt(pageSize),
                scheduleQueryVo);
        return Result.ok(page);
    }

    @PostMapping("schedule/remove")
    public Result removeSchedule(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String hoscode = (String) paramMap.get("hoscode");
        String hosScheduleId = (String) paramMap.get("hosScheduleId");

        scheduleService.remove(hoscode, hosScheduleId);
        return Result.ok();

    }

}
