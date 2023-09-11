package com.probuing.yygh.cmn.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.probuing.yygh.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * ClassName: DictService
 * date: 2023/8/14 16:30
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
public interface DictService extends IService<Dict> {
    List<Dict> findChildData(Long id);

    void importDictData(MultipartFile file);


    void exportDictData(HttpServletResponse resp) throws IOException;

    String getName(String value, String dictCode);

    List<Dict> findByDictCode(String dictCode);
}
