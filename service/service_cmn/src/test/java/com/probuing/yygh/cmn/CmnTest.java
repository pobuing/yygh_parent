package com.probuing.yygh.cmn;

import com.alibaba.excel.EasyExcel;
import com.probuing.yygh.cmn.exceldemo.Stu;
import com.probuing.yygh.cmn.exceldemo.StudentListener;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * ClassName: CmnTest
 * date: 2023/8/14 17:42
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@SpringBootTest
public class CmnTest {

    private String pathName = "/Users/wangxin/Documents/学生列表.xlsx";

    @Test
    public void testWriteExcel() {
        Stu stu = new Stu(101, "张三");
        Stu stu2 = new Stu(102, "李四");
        List<Stu> stus = Arrays.asList(stu, stu2);
        EasyExcel.write(pathName, Stu.class).sheet("测试1")
                .doWrite(stus);
    }


    @Test
    public void testReadExcel() {
        String pathName = "/Users/wangxin/Documents/学生列表.xlsx";

        EasyExcel.read(pathName, new StudentListener()).sheet("测试").doRead();

    }
}
