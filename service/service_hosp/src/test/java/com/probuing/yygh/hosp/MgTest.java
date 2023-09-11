package com.probuing.yygh.hosp;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.probuing.yygh.hosp.testmongo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;
import java.util.List;

/**
 * ClassName: MgTest
 * date: 2023/8/19 19:26
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@SpringBootTest
public class MgTest {
    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    public void test1() {
        User user = new User();
        user.setName("alwaysSad");
        user.setAge(12);
        user.setEmail("kkkk@111.com");
        user.setCreateDate(new Date().toString());
        mongoTemplate.insert(user);
//        mongoTemplate.insert(user, "User");
    }

    @Test
    public void testFindAll() {

        List<? extends User> users = mongoTemplate.findAll(User.class);
        users.forEach(System.out::println);
    }

    @Test
    public void testById() {
        User user = mongoTemplate.findById("64e0a7cd7e020f1c053d12fe", User.class);
        System.out.println("user = " + user);
    }

    @Test
    public void testByCondition() {
        Query query = new Query(Criteria.where("age").is(12).and("name").is("alwaysSad"));
//        query.addCriteria(Criteria.where("age").is(12));

        List<User> userList = mongoTemplate.find(query, User.class);
        userList.forEach(System.out::println);
    }

    @Test
    public void testMohuQuery() {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").regex("al"));
        for (User user : mongoTemplate.find(query, User.class)) {
            System.out.println(user);
        }
    }

    @Test
    public void testPage() {
        long page = 1;
        int limit = 5;
        Query query = new Query(Criteria.where("age").is(20));
        query.skip((page - 1) * limit).limit(limit);
        List<User> userList = mongoTemplate.find(query, User.class);
        userList.forEach(System.out::println);
    }

    @Test
    public void testUpdate() {
        Query query = new Query(Criteria.where("age").is(12));
        Update update = new Update();
        update.set("email", "idea@idea");
        update.set("age", 31);
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, User.class);
        System.out.println("updateResult.getModifiedCount() = " + updateResult.getModifiedCount());
    }


    @Test
    public void testDelete() {
        Query query = new Query();
        query.addCriteria(Criteria.where("age").is(31));
        DeleteResult deleteResult = mongoTemplate.remove(query, User.class);
        System.out.println("deleteResult.getDeletedCount() = " + deleteResult.getDeletedCount());
    }





}
