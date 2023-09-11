package com.probuing.yygh.hosp.controller;

import com.probuing.yygh.common.result.R;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * ClassName: UserLoginController
 * date: 2023/8/12 10:06
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@RestController
@RequestMapping("/admin/hosp/user")
public class UserLoginController {


    @PostMapping("/login")
    public R login() {
        return R.ok().data("token", "admin-token");
    }

    @GetMapping("/info")
    public R info() {
        return R.ok().data("roles", Arrays.asList("admin"))
                .data("introduction", "尚医通平台管理员")
                .data("avatar", "http://5b0988e595225.cdn.sohucs.com/images/20200504/8cc274dd28e547bdae4a45400ad47cb6.gif")
                .data("name", "尚医通");
    }
}
