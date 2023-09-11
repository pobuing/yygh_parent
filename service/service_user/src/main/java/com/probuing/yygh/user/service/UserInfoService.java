package com.probuing.yygh.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.probuing.yygh.model.user.UserInfo;
import com.probuing.yygh.vo.user.LoginVo;
import com.probuing.yygh.vo.user.UserAuthVo;
import com.probuing.yygh.vo.user.UserInfoQueryVo;

import java.util.Map;

/**
 * ClassName: UserInfoService
 * date: 2023/8/23 17:14
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
public interface UserInfoService extends IService<UserInfo> {
    Map<String, Object> login(LoginVo loginVo);

    UserInfo selectWxByOpenId(String openid);

    void userAuth(Long userId, UserAuthVo userAuthVo);

    //用户列表（条件查询带分页）
    IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo);
    /**
     * 用户锁定
     * @param userId
     * @param status 0：锁定 1：正常
     */
    void lock(Long userId, Integer status);

    /**
     * 详情
     * @param userId
     * @return
     */
    Map<String, Object> show(Long userId);

    /**
     * 认证审批
     * @param userId
     * @param authStatus 2：通过 -1：不通过
     */
    void approval(Long userId, Integer authStatus);
}
