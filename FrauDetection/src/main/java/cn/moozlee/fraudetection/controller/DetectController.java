package cn.moozlee.fraudetection.controller;

import cn.moozlee.fraudetection.dto.ModelResponse;
import cn.moozlee.fraudetection.entity.MlModel;
import cn.moozlee.fraudetection.mapper.MlModelMapper;
import cn.moozlee.fraudetection.service.HttpClient;
import cn.moozlee.fraudetection.type_enum.DataType;
import cn.moozlee.fraudetection.entity.DetectRecord;
import cn.moozlee.fraudetection.entity.Result;
import cn.moozlee.fraudetection.mapper.DetectRecordMapper;
import cn.moozlee.fraudetection.service.MinioService;
import cn.moozlee.fraudetection.type_enum.DetectState;
import cn.moozlee.fraudetection.type_enum.ModelAPI;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author moozlee
 * @create 2023-05-07 16:54
 */
@RestController
public class DetectController {

    @Autowired
    MinioService minioService;
    @Autowired
    DetectRecordMapper detectRecordMapper;
    @Autowired
    MlModelMapper mlModelMapper;
    @Autowired
    HttpClient client;
    @Autowired
    ObjectMapper mapper;
    @Value("${minio.bucketName}")
    private String bucketName;

    @PostMapping("/detect/raw")
    public Result rawDataDetect(@RequestParam("file") MultipartFile file, @RequestParam("modelType") Integer modelType) {
        try {
            String tag = minioService.uploadFile(bucketName, file.getOriginalFilename(), file);
            final DetectRecord detectRecord = new DetectRecord(DataType.RAW.getId(), modelType, tag, DetectState.COMMIT.getId());
            detectRecordMapper.insert(detectRecord);
            final MlModel model = mlModelMapper.selectById(modelType);
            if (model == null) {
                return new Result().code(404).message("模型异常");
            }
            Map<String, String> body = new HashMap<>();
            body.put("model_url", model.getUrl());
            body.put("model_id", model.getId().toString());
            body.put("data_url", tag);
            client.postFormDataAsync(ModelAPI.RAW_DETECT, body, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("failed....");
                    recordFailed(detectRecord);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final ResponseBody body = response.body();
                    if (body == null) {
                        recordFailed(detectRecord);
                        return;
                    }
                    final String string = body.string();
                    final ModelResponse res = mapper.readValue(string, ModelResponse.class);
                    if (res.getCode() != 0) {
                        recordFailed(detectRecord);
                        return;
                    }
                    recordSuccess(detectRecord, String.valueOf(res.getData()));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result().code(200).message("已提交");
    }

    @PostMapping("/detect/rate")
    public Result rateDataDetect(@RequestParam("file") MultipartFile file, @RequestParam("modelType") Integer modelType) {
        try {
            String tag = minioService.uploadFile(bucketName, file.getOriginalFilename(), file);
            final DetectRecord detectRecord = new DetectRecord(DataType.RATE.getId(), modelType, tag, DetectState.COMMIT.getId());
            detectRecordMapper.insert(detectRecord);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result().code(200).message("已提交");
    }


    private void recordFailed(DetectRecord record) {
        record.setState(DetectState.FAILED.getId());
        detectRecordMapper.updateById(record);
    }

    private void recordSuccess(DetectRecord record, String resultFile) {
        record.setState(DetectState.SUCCESS.getId());
        record.setResultFile(resultFile);
        detectRecordMapper.updateById(record);
    }
}
