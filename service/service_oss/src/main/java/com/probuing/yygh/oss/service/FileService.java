package com.probuing.yygh.oss.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * ClassName: FileService
 * date: 2023/8/24 16:02
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
public interface FileService {
    /**
     * 文件上传至阿里云
     */
    String upload(MultipartFile file);
}
