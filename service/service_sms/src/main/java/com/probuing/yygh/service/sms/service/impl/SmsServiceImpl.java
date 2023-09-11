package com.probuing.yygh.service.sms.service.impl;

import com.probuing.yygh.service.sms.service.SmsService;
import com.probuing.yygh.service.sms.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: SmsServiceImpl
 * date: 2023/8/23 20:20
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@Service
public class SmsServiceImpl implements SmsService {

    @Override
    public boolean send(String phone, String code) {

        String host = "https://dfsmsv2.market.alicloudapi.com";
        String path = "/data/send_sms_v2";
        String method = "POST";
        String appcode = "2d729b0b67924d0c9dd599d55fa5401d";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("content", "code:" + code);
        bodys.put("template_id", "TPL_0000");
        bodys.put("phone_number", phone);


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
