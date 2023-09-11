package com.probuing.yygh.hosp.testmongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * ClassName: User
 * date: 2023/8/19 19:22
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@Data
@Document("User")
public class User {
    @Id
    private String id;
    private String name;
    private Integer age;
    private String email;
    private String createDate;
}