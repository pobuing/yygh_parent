package com.probuing.yygh.hosp.service;

import com.probuing.yygh.model.hosp.Department;
import com.probuing.yygh.vo.hosp.DepartmentQueryVo;
import com.probuing.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * ClassName: DepartmentService
 * date: 2023/8/20 20:44
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
public interface DepartmentService {
    void save(Map<String, Object> paramMap);

    Page<Department> selectPate(int page, int limit, DepartmentQueryVo departmentQueryVo);


    void remove(String hoscode, String depcode);

    List<DepartmentVo> findDepTree(String hoscode);

    Department findDepartment(String hoscode, String depcode);
}
