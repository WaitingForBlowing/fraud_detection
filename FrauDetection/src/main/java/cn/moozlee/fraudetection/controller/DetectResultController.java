package cn.moozlee.fraudetection.controller;

import cn.moozlee.fraudetection.dto.QueryDto;
import cn.moozlee.fraudetection.entity.DetectRecord;
import cn.moozlee.fraudetection.entity.DetectResult;
import cn.moozlee.fraudetection.entity.Result;
import cn.moozlee.fraudetection.mapper.DetectRecordMapper;
import cn.moozlee.fraudetection.mapper.DetectResultMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author moozlee
 * @create 2023-05-10 14:45
 */
@RestController
public class DetectResultController {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    DetectResultMapper detectResultMapper;

    @GetMapping("/results")
    public Result getRecordPage(@NotNull QueryDto query) {
        DetectResult result = null;
        try {
            result = mapper.readValue(query.getQuery(), DetectResult.class);
        } catch (JsonProcessingException e) {
            return new Result().code(400).message("异常请求");
        }
        final QueryWrapper<DetectResult> wrapper = new QueryWrapper<>();
        if (result.getDataType() != null) {
            wrapper.eq("data_type", result.getDataType());
        }
        if (result.getModelType() != null) {
            wrapper.eq("model_type", result.getModelType());
        }
        if (result.getFirm() != null && !result.getFirm().isEmpty()) {
            wrapper.eq("firm", result.getFirm());
        }
        if (result.getPeriod() != null && !result.getPeriod().isEmpty()) {
            wrapper.eq("period", result.getPeriod());
        }
        if (result.getResult() != null) {
            wrapper.eq("result", result.getResult());
        }
        wrapper.orderByDesc("upload_time");
        final Page<DetectResult> recordPage = detectResultMapper.selectPage(new Page<>(query.getPageNumber(), query.getPageSize()), wrapper);
        return new Result().code(200).message("查询成功").data(recordPage);
    }

    @PutMapping("/result/{id}")
    public Result updateResult(@PathVariable("id") Integer id) {
        final DetectResult result = detectResultMapper.selectById(id);
        if (0 == result.getResult()) {
            result.setResult(1);
        } else {
            result.setResult(0);
        }
        result.setAccuracy(1);
        detectResultMapper.updateById(result);
        return new Result().code(200).message("更改成功");
    }
}
