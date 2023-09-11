package com.probuing.yygh.hosp.repository;

import com.probuing.yygh.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ClassName: DepartmentRepository
 * date: 2023/8/20 20:43
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@Repository
public interface DepartmentRepository extends MongoRepository<Department, String> {

    /**
     * 查询某个医院下的某个科室
     *
     * @param hoscode
     * @param depcode
     * @return
     */
    Department findByHoscodeAndDepcode(String hoscode, String depcode);

    List<Department> findByHoscode(String hoscode);
}
