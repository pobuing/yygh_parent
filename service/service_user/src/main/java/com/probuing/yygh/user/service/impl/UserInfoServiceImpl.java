package com.probuing.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.probuing.yygh.common.exp.YyghException;
import com.probuing.yygh.common.utils.JwtHelper;
import com.probuing.yygh.enums.AuthStatusEnum;
import com.probuing.yygh.model.user.Patient;
import com.probuing.yygh.model.user.UserInfo;
import com.probuing.yygh.user.mapper.UserInfoMapper;
import com.probuing.yygh.user.service.PatientService;
import com.probuing.yygh.user.service.UserInfoService;
import com.probuing.yygh.vo.user.LoginVo;
import com.probuing.yygh.vo.user.UserAuthVo;
import com.probuing.yygh.vo.user.UserInfoQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName: UserInfoServiceImpl
 * date: 2023/8/23 17:20
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        String openid = loginVo.getOpenid();
        if (StringUtils.isEmpty(openid)) {
            //手机号和验证码登录方式
            return phoneAndCodeLogin(loginVo);
        } else {
            //微信用户绑定手机号
            return wxBundlePhone(loginVo);
        }
    }

    private Map<String, Object> wxBundlePhone(LoginVo loginVo) {
        //微信登录+绑定手机号方式
        //根据openId查询用户
        UserInfo userInfo_wx = selectWxByOpenId(loginVo.getOpenid());
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        String phone = loginVo.getPhone();
        queryWrapper.eq("phone", phone);
        //根据手机号查询
        UserInfo userInfo_ph = baseMapper.selectOne(queryWrapper);
        if (userInfo_ph == null) {
            //表示该账户没有手机号 绑定手机号
            userInfo_wx.setPhone(phone);
            baseMapper.updateById(userInfo_wx);
        } else {
            //手机号已经存在，合并两条数据
            userInfo_wx.setName(userInfo_ph.getName());
            userInfo_wx.setCertificatesType(userInfo_ph.getCertificatesType());
            userInfo_wx.setCertificatesNo(userInfo_ph.getCertificatesNo());
            userInfo_wx.setCertificatesUrl(userInfo_ph.getCertificatesUrl());
            userInfo_wx.setAuthStatus(userInfo_ph.getAuthStatus());
            userInfo_wx.setPhone(phone);
            //删除旧数据
            baseMapper.deleteById(userInfo_ph.getId());
            //更新新的数据
            baseMapper.updateById(userInfo_wx);
        }
        Map<String, Object> map = getUserInfoMap(userInfo_wx);
        return map;
    }

    private static Map<String, Object> getUserInfoMap(UserInfo userInfo_wx) {
        Map<String, Object> map = new HashMap<>();
        String name = userInfo_wx.getName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo_wx.getNickName();
            if (StringUtils.isEmpty(name)) {
                name = userInfo_wx.getPhone();
            }
        }
        map.put("name", name);
        //创建用户令牌
        String token = JwtHelper.createToken(userInfo_wx.getId(), name);
        map.put("token", token);
        return map;
    }

    private Map<String, Object> phoneAndCodeLogin(LoginVo loginVo) {
        //手机号+验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new YyghException(20001, "手机号和验证码不能为空");
        }


        //从redis中取出验证码
        String code_redis = redisTemplate.boundValueOps(phone).get();
        if (!code.equalsIgnoreCase(code_redis)) {
            throw new YyghException(20001, "验证码错误");
        }
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);
        if (userInfo == null) {
            //新用户，直接注册
            userInfo = new UserInfo();
            userInfo.setPhone(phone);
            userInfo.setStatus(1);
            userInfo.setAuthStatus(0);
            userInfo.setCreateTime(new Date());
            userInfo.setUpdateTime(new Date());
            baseMapper.insert(userInfo);
        }

        //判断用户的状态是否被锁定
        if (userInfo.getStatus() == 0) {
            throw new YyghException(20001, "用户被锁定，不能登录");
        }


        return getUserInfoMap(userInfo);
    }

    @Override
    public UserInfo selectWxByOpenId(String openid) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);

        return userInfo;
    }

    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        //实名认证
        // 根据userId 查询Userinfo
        UserInfo userInfo = baseMapper.selectById(userId);
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        baseMapper.updateById(userInfo);

    }

    @Override
    public IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        if (userInfoQueryVo != null) {
            String keyword = userInfoQueryVo.getKeyword();
            Integer status = userInfoQueryVo.getStatus();
            Integer authStatus = userInfoQueryVo.getAuthStatus();
            String createTimeBegin = userInfoQueryVo.getCreateTimeBegin();
            String createTimeEnd = userInfoQueryVo.getCreateTimeEnd();
            if (!StringUtils.isEmpty(keyword)) {
                queryWrapper.like("name", keyword);
            }
            if (!StringUtils.isEmpty(status)) {
                queryWrapper.eq("status", status);
            }
            if (!StringUtils.isEmpty(authStatus)) {
                queryWrapper.eq("auth_status", authStatus);
            }
            if (!StringUtils.isEmpty(createTimeBegin)) {
                queryWrapper.ge("create_time", createTimeBegin);
            }
            if (!StringUtils.isEmpty(createTimeEnd)) {
                queryWrapper.le("create_time", createTimeBegin);
            }
        }
        page(pageParam, queryWrapper);
        pageParam.getRecords().forEach(userInfo -> {
            packUserInfo(userInfo);
        });
        return pageParam;
    }

    @Override
    public void lock(Long userId, Integer status) {
        if(status.intValue() == 0 || status.intValue() == 1) {
            UserInfo userInfo = this.getById(userId);
            userInfo.setStatus(status);
            this.updateById(userInfo);
        }
    }

    @Autowired
    private PatientService patientService;
    @Override
    public Map<String, Object> show(Long userId) {
        Map<String,Object> map = new HashMap<>();
        //根据userid查询用户信息
        UserInfo userInfo = baseMapper.selectById(userId);
        packUserInfo(userInfo);
        map.put("userInfo",userInfo);
        //根据userid查询就诊人信息
        List<Patient> patientList = patientService.findAllUserId(userId);
        map.put("patientList",patientList);
        return map;
    }

    @Override
    public void approval(Long userId, Integer authStatus) {
        if(authStatus.intValue()==2 || authStatus.intValue()==-1) {
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }
    }


    private void packUserInfo(UserInfo userInfo) {
        Integer status = userInfo.getStatus();
        Integer authStatus = userInfo.getAuthStatus();
        String statusString = status == 0 ? "锁定" : "正常";
        String authStatusString = AuthStatusEnum.getStatusNameByStatus(authStatus);
        userInfo.getParam().put("statusString", statusString);
        userInfo.getParam().put("authStatusString", authStatusString);
    }


}
