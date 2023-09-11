package com.probuing.yygh.hosp.repository;

import com.probuing.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ClassName: HospitalRepository
 * date: 2023/8/20 19:05
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@Repository
public interface HospitalRepository extends MongoRepository<Hospital, String> {
    Hospital findByHoscode(String hoscode);

    List<Hospital> findByHosnameLike(String hosname);
}
