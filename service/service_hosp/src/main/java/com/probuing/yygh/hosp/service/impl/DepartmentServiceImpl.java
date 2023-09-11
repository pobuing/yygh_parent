package com.probuing.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.probuing.yygh.common.exp.YyghException;
import com.probuing.yygh.hosp.repository.DepartmentRepository;
import com.probuing.yygh.hosp.service.DepartmentService;
import com.probuing.yygh.model.hosp.Department;
import com.probuing.yygh.vo.hosp.DepartmentQueryVo;
import com.probuing.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ClassName: DepartmentServiceImpl
 * date: 2023/8/20 20:45
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void save(Map<String, Object> paramMap) {
        //department 存在--->update 不存在---->create
        Department department = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Department.class);
        Department department_mg = departmentRepository.findByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());
        if (department_mg == null) {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
        } else {
            department.setId(department_mg.getId());
            department.setCreateTime(department_mg.getCreateTime());
            department.setUpdateTime(new Date());
        }
        departmentRepository.save(department);
    }

    @Override
    public Page<Department> selectPate(int page, int limit, DepartmentQueryVo departmentQueryVo) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createTime"));
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo, department);
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withIgnoreCase(true)
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Department> example = Example.of(department, exampleMatcher);
        Page<Department> pageResult = departmentRepository.findAll(example, pageable);
        return pageResult;
    }

    @Override
    public void remove(String hoscode, String depcode) {
        //查询科室
        Department department = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);
        //科室存在 通过id删除
        if (department != null) {
            String id = department.getId();
            departmentRepository.deleteById(id);
        } else {
            throw new YyghException(20001, "科室不存在");
        }
    }

    @Override
    public List<DepartmentVo> findDepTree(String hoscode) {
        //查询该hoscode对应的所有小科室
        List<Department> departmentList = departmentRepository.findByHoscode(hoscode);
        //根据bigcode分组
        Function<Department, String> fun = new Function<Department, String>() {
            @Override
            public String apply(Department department) {
                return department.getBigcode();
            }
        };
        Map<String, List<Department>> collectMap = departmentList.stream()
                .collect(Collectors.groupingBy(fun));
        ArrayList<DepartmentVo> departmentVos = new ArrayList<>();
        collectMap.entrySet().forEach(entry -> {
            String key = entry.getKey();
            List<Department> value = entry.getValue();
            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setDepcode(key);//大科室编号
            departmentVo.setDepname(value.get(0).getBigname());
            departmentVo.setChildren(this.transferDepartmentVo(value));
            departmentVos.add(departmentVo);
        });
//                .collect(Collectors.groupingBy(Department::getBigcode))
        return departmentVos;
    }

    @Override
    public Department findDepartment(String hoscode, String depcode) {
        return departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);
    }

    private List<DepartmentVo> transferDepartmentVo(List<Department> value) {
        List<DepartmentVo> departmentVoList = new ArrayList<>();
        value.forEach(department -> {
            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setDepname(department.getDepname());//小科室名称
            departmentVo.setDepcode(department.getDepcode());
            departmentVoList.add(departmentVo);
        });
        return departmentVoList;
    }


}
