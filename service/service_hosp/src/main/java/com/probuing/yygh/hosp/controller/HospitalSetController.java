package com.probuing.yygh.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.probuing.yygh.common.result.R;
import com.probuing.yygh.hosp.service.HospitalSetService;
import com.probuing.yygh.model.hosp.HospitalSet;
import com.probuing.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: HospitalController
 * date: 2023/8/10 18:10
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {
    @Autowired
    private HospitalSetService hospitalSetService;

    @ApiOperation(value = "分页条件医院设置列表")
    @PostMapping("{page}/{limit}")
    public R pageQuery(@PathVariable("page")
                       @ApiParam(name = "page", value = "当前页码", required = true)
                       Long page,
                       @PathVariable("limit")
                       @ApiParam(name = "limit", value = "每页记录数", required = true)
                       Long limit,
                       @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) {
        Page<HospitalSet> hospitalSetPage = new Page<>(page, limit);

        QueryWrapper<HospitalSet> queryWrapper = null;
        if (hospitalSetQueryVo != null) {
            queryWrapper = new QueryWrapper<>();
            String hosname = hospitalSetQueryVo.getHosname();
            String hoscode = hospitalSetQueryVo.getHoscode();
            if (!StringUtils.isEmpty(hoscode)) {
                queryWrapper.eq("hoscode", hoscode);
            }
            if (!StringUtils.isEmpty(hosname)) {
                queryWrapper.like("hosname", hosname);
            }
        }
        hospitalSetService.page(hospitalSetPage, queryWrapper);
        long total = hospitalSetPage.getTotal();
        List<HospitalSet> records = hospitalSetPage.getRecords();
        return R.ok().data("total", total).data("rows", records);
    }

    @GetMapping("findAll")
    @ApiOperation(value = "医院设置列表")
    public R findAll() {
        List<HospitalSet> list = hospitalSetService.list();
        /*try {
            int i = 1 / 0;
        } catch (Exception e) {
            throw new YyghException(20001, "出现了自定义异常");
        }*/
        return R.ok().data("list", list);
    }

    @ApiOperation(value = "医院设置删除")
    @DeleteMapping("{id}")
    public R removeById(@ApiParam(name = "id", value = "医院设置主键", required = true) @PathVariable Long id) {
        boolean b = hospitalSetService.removeById(id);
        return b ? R.ok() : R.error();
    }


    @ApiOperation("分页医院设置列表")
    @GetMapping("{page}/{limit}")
    public R pageList(@PathVariable("page")
                      @ApiParam(name = "page", value = "当前页码", required = true)
                      Long page,
                      @PathVariable("limit")
                      @ApiParam(name = "limit", value = "每页记录数", required = true)
                      Long limit) {
        Page<HospitalSet> pageParam = new Page<>(page, limit);
        hospitalSetService.page(pageParam);
        //获取分页后的数据
        List<HospitalSet> records = pageParam.getRecords();
        //获取分页总记录数
        long total = pageParam.getTotal();
        return R.ok().data("rows", records).data("total", total);
    }


    @ApiOperation(value = "开通医院设置")
    @PostMapping("save")
    public R save(@ApiParam(name = "hospitalSet", value = "医院设置对象")
                  @RequestBody HospitalSet hospitalSet) {
        //查询hoscode是否存在
        String hoscode = hospitalSet.getHoscode();
        if (StringUtils.isEmpty(hoscode)) {
            return R.error().message("医院编号不能为空");
        }

        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hoscode", hoscode);
        queryWrapper.last("OR (is_deleted=1 AND hoscode=" + hoscode + ")");
        int count = hospitalSetService.count(queryWrapper);
        if (count >= 1) {
            return R.error().message("该医院已经开通");
        }
        hospitalSet.setStatus(1);//开通医院设置之后，状态设置为1----可用状态
        boolean save = hospitalSetService.save(hospitalSet);

        return save ? R.ok().message("开通成功") : R.error().message("开通失败");
    }

    @ApiOperation(value = "根据ID查询医院设置")
    @GetMapping("getHospSet/{id}")
    public R getById(@ApiParam(name = "id", value = "医院设置ID") @PathVariable("id") String id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return R.ok().data("item", hospitalSet);
    }

    @ApiOperation(value = "根据ID修改医院设置")
    @PostMapping("updateHospSet")
    public R updateById(@ApiParam(name = "hospitalSet", value = "医院设置对象", required = true)
                        @RequestBody HospitalSet hospitalSet) {

        hospitalSetService.updateById(hospitalSet);
        return R.ok();
    }

    @ApiOperation(value = "批量删除医院设置")
    @DeleteMapping("batchRemove")
    public R batchRemoveHospitalSet(@ApiParam(name = "ids", value = "批量删除id集合", required = true)
                                    @RequestBody List<Long> ids) {
        hospitalSetService.removeByIds(ids);
        return R.ok();
    }

    @ApiOperation("设置医院锁定和解锁")
    @PutMapping("lockHospitalSet/{id}/{status}")
    public R lockHospitalSet(
            @ApiParam(name = "id", value = "医院设置ID")
            @PathVariable("id") Long id,
            @ApiParam(name = "status", value = "医院设置状态")
            @PathVariable("status") Integer status
    ) {
        //根据id查询数据
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        if (hospitalSet == null) {
            return R.error().message("该医院设置不存在");
        }
        //判断status参数
        if (status != 0 && status != 1) {
            return R.error().message("status只能是0或1");
        }
        hospitalSet.setStatus(status);
        boolean b = hospitalSetService.updateById(hospitalSet);
        return b ? R.ok() : R.error();
    }


}
