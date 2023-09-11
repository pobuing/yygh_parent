package com.probuing.yygh.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.probuing.yygh.common.result.R;
import com.probuing.yygh.common.utils.JwtHelper;
import com.probuing.yygh.model.user.UserInfo;
import com.probuing.yygh.user.service.UserInfoService;
import com.probuing.yygh.user.utils.ConstantPropertiesUtil;
import com.probuing.yygh.user.utils.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/user/wx")
public class WeixinApiController {
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取微信登录参数
     */
    @GetMapping("getLoginParam")
    @ResponseBody
    public R genQrConnect(HttpSession session) throws UnsupportedEncodingException {
        String redirectUri = URLEncoder.encode(ConstantPropertiesUtil.WX_OPEN_REDIRECT_URL, "UTF-8");
        Map<String, Object> map = new HashMap<>();
        map.put("appid", ConstantPropertiesUtil.WX_OPEN_APP_ID);
        map.put("id", "weixinLogin");
        map.put("redirectUri", redirectUri);
        map.put("scope", "snsapi_login");
        String state = System.currentTimeMillis() + "";
        map.put("state", state);//System.currentTimeMillis()+""
        session.setAttribute("state", state);
        return R.ok().data(map);
    }

    @GetMapping("callback")
    public String callBack(String code, String state, HttpSession session) {
        System.out.println("临时票据" + code);
        //调用微信端接口来查询 微信用户的 openId
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                ConstantPropertiesUtil.WX_OPEN_APP_SECRET,
                code);
        try {
            String accesstokenInfo = HttpClientUtils.get(accessTokenUrl);
            //string ---> jsonObj
            JSONObject jsonObject = JSONObject.parseObject(accesstokenInfo);
            String access_token = jsonObject.getString("access_token");
            String openid = jsonObject.getString("openid");
            //从user_info表中查询openId是否存在
            UserInfo userInfo = userInfoService.selectWxByOpenId(openid);
            if (userInfo == null) {
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, access_token, openid);
                String resultInfo = HttpClientUtils.get(userInfoUrl);
                JSONObject resultUserInfoJson = JSONObject.parseObject(resultInfo);
                //解析用户信息
                //用户昵称
                String nickname = resultUserInfoJson.getString("nickname");
                //用户头像
                String headimgurl = resultUserInfoJson.getString("headimgurl");

                //注册
                userInfo = new UserInfo();
                userInfo.setOpenid(openid);
                userInfo.setNickName(nickname);
                userInfo.setName("");
                userInfo.setStatus(1);
                userInfoService.save(userInfo);
            }
            String name = userInfo.getName();
            if (StringUtils.isEmpty(name)) {
                //name为空 nickName
                name = userInfo.getNickName();
                if (StringUtils.isEmpty(name)) {
                    name = userInfo.getPhone();
                }
            }
            //创建JWT Token
            String token = JwtHelper.createToken(userInfo.getId(), name);
            //判断查询手机号是否存在 存在表示已经绑定过手机号 不存在需要绑定手机号
            String phone = userInfo.getPhone();
            if (StringUtils.isEmpty(phone)) {
                //需要
            }else{
                //不需要

            }
            return "redirect:http://localhost:3000/weixin/callback?name="+name+
                    "&token="+token+"&openid="+(StringUtils.isEmpty(phone)?openid:"");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}