package com.probuing.yygh.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.probuing.yygh.common.result.R;
import com.probuing.yygh.model.user.UserInfo;
import com.probuing.yygh.user.service.UserInfoService;
import com.probuing.yygh.vo.user.UserInfoQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ClassName: UserInfoController
 * date: 2023/8/24 19:37
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@RestController
@RequestMapping("/admin/user")
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    //用户列表（条件查询带分页）
    @GetMapping("{page}/{limit}")
    public R list(@PathVariable Long page,
                  @PathVariable Long limit,
                  UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> userInfoPage = new Page<UserInfo>(page, limit);
        IPage<UserInfo> userInfoIPage = userInfoService.selectPage(userInfoPage, userInfoQueryVo);
        return R.ok().data("pageModel", userInfoIPage);
    }


    @ApiOperation(value = "锁定")
    @GetMapping("lock/{userId}/{status}")
    public R lock(@PathVariable("userId") Long userId, @PathVariable("status") Integer status) {
        userInfoService.lock(userId, status);
        return R.ok();
    }

    //用户详情
    @GetMapping("show/{userId}")
    public R show(@PathVariable Long userId) {
        Map<String, Object> map = userInfoService.show(userId);
        return R.ok().data(map);
    }

    //认证审批
    @GetMapping("approval/{userId}/{authStatus}")
    public R approval(@PathVariable Long userId,@PathVariable Integer authStatus) {
        userInfoService.approval(userId,authStatus);
        return R.ok();
    }
}
