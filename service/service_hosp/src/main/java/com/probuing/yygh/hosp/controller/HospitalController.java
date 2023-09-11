package com.probuing.yygh.hosp.controller;

import com.probuing.yygh.common.result.R;
import com.probuing.yygh.hosp.service.HospitalService;
import com.probuing.yygh.model.hosp.BookingRule;
import com.probuing.yygh.model.hosp.Hospital;
import com.probuing.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * ClassName: HospitalController
 * date: 2023/8/21 16:37
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@RestController
@Api("医院接口")
@RequestMapping("/admin/hosp/hospital")
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;

    @ApiOperation("获取医院列表接口")
    @PostMapping("{page}/{limit}")
    public R index(@PathVariable Integer page,
                   @PathVariable Integer limit,
                   @RequestBody HospitalQueryVo hospitalQueryVo) {
        Page<Hospital> pageResult = hospitalService.selectPage(page, limit, hospitalQueryVo);
        return R.ok().data("pages", pageResult);
    }

    @GetMapping("show/{id}")
    public R show(@PathVariable String id) {
        Hospital hospital = hospitalService.show(id);
        BookingRule bookingRule = hospital.getBookingRule();
        HashMap<String, Object> map = new HashMap<>();
        map.put("hospital", hospital);
        map.put("bookingRule", bookingRule);
        return R.ok().data("hospital", map);
    }


    @GetMapping("updateStatus/{id}/{status}")
    public R updateStatus(@PathVariable String id,@PathVariable Integer status) {
        hospitalService.updateStatus(id, status);
        return R.ok();
    }

}
