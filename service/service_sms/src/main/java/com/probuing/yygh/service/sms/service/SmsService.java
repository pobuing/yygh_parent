package com.probuing.yygh.service.sms.service;

/**
 * ClassName: SmsService
 * date: 2023/8/23 20:19
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
public interface SmsService {
    public boolean send(String phone, String code);
}
