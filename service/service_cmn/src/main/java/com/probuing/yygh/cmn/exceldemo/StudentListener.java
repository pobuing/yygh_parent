package com.probuing.yygh.cmn.exceldemo;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

/**
 * ClassName: StudentListener
 * date: 2023/8/14 18:12
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
//逐行读取excel文档
public class StudentListener extends AnalysisEventListener<Stu> {
    @Override
    public void invoke(Stu stu, AnalysisContext analysisContext) {
        System.out.println("stu = " + stu);

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        System.out.println("所有行读取完成");
    }
}
