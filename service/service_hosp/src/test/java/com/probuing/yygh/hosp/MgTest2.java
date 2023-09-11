package com.probuing.yygh.hosp;

import com.probuing.yygh.hosp.testmongo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * ClassName: MgTest2
 * date: 2023/8/19 21:59
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@SpringBootTest
public class MgTest2 {
    @Autowired
    private MongoRepository mongoRepository;

    @Test
    public void testRepository() {
//        mongoRepository.save() 添加/(根据id修改)一个文档
        //分页 0，5
//        mongoRepository.findAll(PageRequest.of(0, 5));
        User user = new User();
        user.setName("tom");
        user.setAge(20);
        Example<User> example = Example.of(user);//where age = 20 and name="tom"
        List list = mongoRepository.findAll(example);
        User mhUser = new User();
        mhUser.setName("aasda");
        // name like aasda
        ExampleMatcher matcher = ExampleMatcher.matching()
                //忽略大小写
                .withIgnoreCase(true)
                //字符串包含条件
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<User> likeExample = Example.of(mhUser, matcher);
        mongoRepository.findAll(likeExample);
    }
}
