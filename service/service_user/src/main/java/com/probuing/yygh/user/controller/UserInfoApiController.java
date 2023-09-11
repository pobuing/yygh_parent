package com.probuing.yygh.user.controller;

import com.probuing.yygh.common.result.R;
import com.probuing.yygh.common.utils.AuthContextHolder;
import com.probuing.yygh.enums.AuthStatusEnum;
import com.probuing.yygh.model.user.UserInfo;
import com.probuing.yygh.user.service.UserInfoService;
import com.probuing.yygh.vo.user.LoginVo;
import com.probuing.yygh.vo.user.UserAuthVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * ClassName: UserInfoApiController
 * date: 2023/8/23 17:22
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@RestController
@RequestMapping("/api/user")
public class UserInfoApiController {

    @Autowired
    UserInfoService userInfoService;

    @PostMapping("login")
    public R login(@RequestBody LoginVo loginVo) {
        Map<String, Object> map = userInfoService.login(loginVo);
        return R.ok().data(map);
    }

    //用户认证接口
    @PostMapping("auth/userAuth")
    public R userAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request) {
        //传递两个参数，第一个参数用户id，第二个参数认证数据vo对象
        userInfoService.userAuth(AuthContextHolder.getUserId(request), userAuthVo);
        return R.ok();
    }

    //获取用户id信息接口
    @GetMapping("auth/getUserInfo")
    public R getUserInfo(HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        UserInfo userInfo = userInfoService.getById(userId);
        Integer authStatus = userInfo.getAuthStatus();
        String authStatusString = AuthStatusEnum.getStatusNameByStatus(authStatus);
        userInfo.getParam().put("authStatusString", authStatusString);
        return R.ok().data("userInfo", userInfo);
    }
}
