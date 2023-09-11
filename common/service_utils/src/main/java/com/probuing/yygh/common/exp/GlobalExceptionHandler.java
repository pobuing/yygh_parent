package com.probuing.yygh.common.exp;

import com.probuing.yygh.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ClassName: GlobalExceptionHandler
 * date: 2023/8/11 16:24
 *
 * @author wangxin
 * @version 1.0
 * Description:全局统一异常处理
 * Good Luck
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public R error(Exception e) {
        return R.error().message(e.getMessage());
    }

    /**
     * 特定处理异常
     *
     * @param e 处理算数异常
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = ArithmeticException.class)
    public R error(ArithmeticException e) {

        return R.error().message("ArithmeticException：-->" + e.getMessage());
    }

    /**
     * 特定处理异常
     *
     * @param e 处理算数异常
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = YyghException.class)
    public R error(YyghException e) {
        log.error(e.getMsg());
        return R.error().message("YyghException：-->" + e.getMsg());
    }
}
