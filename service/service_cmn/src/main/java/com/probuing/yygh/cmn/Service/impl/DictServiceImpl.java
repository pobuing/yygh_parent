package com.probuing.yygh.cmn.Service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.probuing.yygh.cmn.Service.DictService;
import com.probuing.yygh.cmn.excellistener.DictReadListener;
import com.probuing.yygh.cmn.mapper.DictMapper;
import com.probuing.yygh.model.cmn.Dict;
import com.probuing.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: DictServiceImpl
 * date: 2023/8/14 16:30
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    @Autowired
    private DictMapper dictMapper;

    @Autowired
    private DictReadListener dictReadListener;

    @Autowired
    private RedisTemplate redisTemplate;

    @Cacheable(value = "dict_cache", key = "'dict_cache_'+#id")
    @Override
    public List<Dict> findChildData(Long id) {
        //从redis中查询是否存在该部分数据
       /* List<Dict> list = (List<Dict>) redisTemplate.boundValueOps("dict_cache_" + id).get();
        if (list != null && list.size() > 0) {
            return list;
        }*/
        //构造查询条件
        QueryWrapper queryWrapper = new QueryWrapper<Dict>();
        queryWrapper.eq("parent_id", id);
//        dictMapper.selectList()
        List<Dict> list = this.list(queryWrapper);
        list.forEach(dict -> {
            //判断是否有下级
            hasChildren(dict);
        });
//        redisTemplate.boundValueOps("dict_cache_" + id).set(list, 5L, TimeUnit.MINUTES);
        return list;//向指定的命名空间下添加一组key-value
    }

    //allEntries删除所有数据 beforeInvocation 在方法执行前就删除
    @CacheEvict(value = "dict_cache", allEntries = true, beforeInvocation = true)
    @Override
    public void importDictData(MultipartFile file) {
        try {
            //导入数据的时候 清除缓存
//            Set keys = redisTemplate.keys("dict_cache_*");
//            redisTemplate.delete(keys);
            InputStream ios = file.getInputStream();//获取当前文件的输入流
            EasyExcel.read(ios, DictEeVo.class, dictReadListener).sheet().doRead();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void exportDictData(HttpServletResponse response) throws IOException {
        //查询数据库中的所有数据
        List<Dict> dictList = dictMapper.selectList(null);
        ArrayList<DictEeVo> dictEeVoList = new ArrayList<>();
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("尚医通数据字典", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        dictList.forEach(dict -> {
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(dict, dictEeVo);
            dictEeVoList.add(dictEeVo);
        });

        EasyExcel.write(response.getOutputStream(), DictEeVo.class)
                .sheet().doWrite(dictEeVoList);

    }

    @Override
    public String getName(String value, String dictCode) {
        Dict dict;
        if (!StringUtils.isEmpty(dictCode)) {

            Dict dictByDictCode = getDictByDictCode(dictCode);
            Long parentId = dictByDictCode.getId();
            QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("value", value);
            queryWrapper.eq("parent_id", parentId);
            dict = dictMapper.selectOne(queryWrapper);
        } else {
            //查询省市区 直接根据value查询
            QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("value", value);
            dict = dictMapper.selectOne(queryWrapper);
        }
        return dict.getName();

    }

    @Override
    public List<Dict> findByDictCode(String dictCode) {
        Dict dictByDictCode = getDictByDictCode(dictCode);
        Long id = dictByDictCode.getId();
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        List<Dict> list = dictMapper.selectList(queryWrapper);
        return list;
    }

    private Dict getDictByDictCode(String dictCode) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_code", dictCode);
        Dict dict = dictMapper.selectOne(queryWrapper);
        return dict;
    }

    private void hasChildren(Dict dict) {
        Long id1 = dict.getId();
        QueryWrapper queryWrapper1 = new QueryWrapper<Dict>();
        queryWrapper1.eq("parent_id", id1);
        Integer count = baseMapper.selectCount(queryWrapper1);
        dict.setHasChildren(count > 0);
    }
}
