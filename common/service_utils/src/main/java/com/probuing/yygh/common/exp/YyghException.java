package com.probuing.yygh.common.exp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: YyghException
 * date: 2023/8/11 16:29
 *
 * @author wangxin
 * @version 1.0
 * Description:自定义异常
 * Good Luck
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YyghException extends RuntimeException {
    private Integer code;
    private String msg;
}
