package com.probuing.yygh.cmn.excellistener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.probuing.yygh.cmn.mapper.DictMapper;
import com.probuing.yygh.model.cmn.Dict;
import com.probuing.yygh.vo.cmn.DictEeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ClassName: DictReadListener
 * date: 2023/8/14 18:33
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */

@Component
@Slf4j
public class DictReadListener extends AnalysisEventListener<DictEeVo> {

    @Autowired
    DictMapper dictMapper;

    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        //将读取到的dictEvo插入到数据库中
        log.info("excel读取数据", dictEeVo);
        //dictEeVo ---> Dict
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo, dict);
        dictMapper.insert(dict);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
