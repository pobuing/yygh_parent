package com.probuing.yygh.cmn.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * ClassName: DictFeignClient
 * date: 2023/8/21 17:21
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@FeignClient("service-cmn")
public interface DictFeignClient {
    @GetMapping("/admin/cmn/dict/getName/{value}/{dictCode}")
    public String getNameByValueAndDictCode(@PathVariable String value,
                                            @PathVariable String dictCode);

    @GetMapping("/admin/cmn/dict/getName/{value}")
    public String getNameByValueAndDictCode(@PathVariable String value);
}
