package com.probuing.yygh.hosp.testmongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * ClassName: UserRepository
 * date: 2023/8/19 21:58
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
}
