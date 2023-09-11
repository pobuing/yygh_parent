package com.probuing.yygh.cmn.exceldemo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: Stu
 * date: 2023/8/14 17:39
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stu {
    @ExcelProperty(index = 0, value = "学生编号")
    private Integer number;
    @ExcelProperty(index = 1, value = "学生姓名")
    private String name;
}
