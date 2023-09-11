package com.probuing.yygh.hosp.controller;

import com.probuing.yygh.common.result.R;
import com.probuing.yygh.hosp.service.DepartmentService;
import com.probuing.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName: DepartmentController
 * date: 2023/8/22 16:50
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@RestController
@RequestMapping("/admin/hosp/department")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    @GetMapping("getDeptList/{hoscode}")
    public R getDeptList(@PathVariable String hoscode) {
        List<DepartmentVo> depTree = departmentService.findDepTree(hoscode);
        return R.ok().data("list", depTree);
    }
}
