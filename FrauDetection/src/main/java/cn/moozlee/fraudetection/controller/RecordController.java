package cn.moozlee.fraudetection.controller;

import cn.moozlee.fraudetection.dto.QueryDto;
import cn.moozlee.fraudetection.entity.DetectRecord;
import cn.moozlee.fraudetection.entity.Result;
import cn.moozlee.fraudetection.mapper.DetectRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author moozlee
 * @create 2023-05-10 11:39
 */
@RestController
public class RecordController {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    DetectRecordMapper detectRecordMapper;

    @GetMapping("/records")
    public Result getRecordPage(@NotNull QueryDto query) {
        DetectRecord record = null;
        try {
            record = mapper.readValue(query.getQuery(), DetectRecord.class);
        } catch (JsonProcessingException e) {
            return new Result().code(400).message("异常请求");
        }
        final QueryWrapper<DetectRecord> wrapper = new QueryWrapper<>();
        if (record.getDataType() != null) {
            wrapper.eq("data_type", record.getDataType());
        }
        if (record.getModelType() != null) {
            wrapper.eq("model_type", record.getModelType());
        }
        if (record.getState() != null) {
            wrapper.eq("state", record.getState());
        }
        wrapper.orderByDesc("upload_time");
        final Page<DetectRecord> recordPage = detectRecordMapper.selectPage(new Page<>(query.getPageNumber(), query.getPageSize()), wrapper);
        return new Result().code(200).message("查询成功").data(recordPage);
    }
}
