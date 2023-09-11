package com.probuing.yygh.oss.control;

import com.probuing.yygh.common.result.R;
import com.probuing.yygh.oss.service.FileService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * ClassName: FileUploadController
 * date: 2023/8/24 16:03
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@Api(description = "阿里云文件管理")
@RestController
@RequestMapping("/admin/oss/file")
public class FileUploadController {

    @Autowired
    private FileService fileService;

    @PostMapping("upload")
    public R upload(@RequestParam("file") MultipartFile file) {
        String url = fileService.upload(file);
        return R.ok().message("文件上传成功").data("url", url);
    }
}
