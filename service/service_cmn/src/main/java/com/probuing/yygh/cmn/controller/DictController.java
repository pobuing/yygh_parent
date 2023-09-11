package com.probuing.yygh.cmn.controller;

import com.probuing.yygh.cmn.Service.DictService;
import com.probuing.yygh.common.result.R;
import com.probuing.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * ClassName: DictController
 * date: 2023/8/14 16:31
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@Api("数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {
    @Autowired
    private DictService dictService;

    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public R findChildData(@ApiParam(name = "id", value = "数据字典id")
                           @PathVariable Long id) {
        List<Dict> childList = dictService.findChildData(id);
        return R.ok().data("list", childList);
    }

    @PostMapping("importData")
    @ApiOperation("文件导入到数据库接口")
    public R importData(@ApiParam(name = "file", value = "上传的数据文件") MultipartFile file) {
        dictService.importDictData(file);
        return R.ok();
    }

    @GetMapping("exportData")
    @ApiOperation("文件导出接口")
    public void exportData(HttpServletResponse resp) {
        try {
            dictService.exportDictData(resp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ApiOperation("获取数据字典名称,适用根据value查询医院等级")
    @GetMapping("getName/{value}/{dictCode}")
    public String getNameByValueAndDictCode(@PathVariable String value,
                                            @PathVariable String dictCode) {
        return dictService.getName(value, dictCode);
    }

    @ApiOperation("获取数据字典名称,适用根据省市区名字")
    @GetMapping("getName/{value}")
    public String getNameByValueAndDictCode(@PathVariable String value) {
        return dictService.getName(value,null);
    }

    @GetMapping("findByDictCode/{dictCode}")
    public R findByDictCode(@PathVariable String dictCode) {
        List<Dict> list = dictService.findByDictCode(dictCode);
        return R.ok().data("list",list);

    }
}
