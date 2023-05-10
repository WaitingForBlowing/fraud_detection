package cn.moozlee.fraudetection.controller;

import cn.moozlee.fraudetection.dto.ModelResponse;
import cn.moozlee.fraudetection.dto.QueryDto;
import cn.moozlee.fraudetection.entity.MlModel;
import cn.moozlee.fraudetection.entity.Result;
import cn.moozlee.fraudetection.mapper.MlModelMapper;
import cn.moozlee.fraudetection.service.HttpClient;
import cn.moozlee.fraudetection.service.MinioService;
import cn.moozlee.fraudetection.type_enum.ModelAPI;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.StatObjectResponse;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author moozlee
 * @create 2023-05-06 16:12
 */
@RestController
public class ModelInfoController {
    @Autowired
    MlModelMapper mlModelMapper;
    @Autowired
    MinioService minioService;
    @Autowired
    HttpClient client;
    @Autowired
    ObjectMapper mapper;
    @Value("${minio.bucketName}")
    private String bucketName;


    @GetMapping("/models/page")
    public Result getModelPage(@NotNull QueryDto query) {
        IPage<MlModel> page = mlModelMapper.selectPage(new Page<>(query.getPageNumber(), query.getPageSize()), new QueryWrapper<>());
        return new Result().code(200).message("查询成功").data(page);
    }

    @GetMapping("/models")
    public Result getModelList() {
        final List<MlModel> models = mlModelMapper.selectList(new QueryWrapper<>());
        return new Result().code(200).message("查询成功").data(models);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("id") Integer id, HttpServletResponse request) throws UnsupportedEncodingException {
        final MlModel mlModel = mlModelMapper.selectById(id);
        final String fileName = mlModel.getUrl();
        InputStream inputStream = null;
        Resource resource = null;
        StatObjectResponse objectStat = null;
        try {
            inputStream = minioService.downloadFile(bucketName, fileName);
            resource = new InputStreamResource(inputStream);
            objectStat = minioService.getObjectStat(bucketName, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (inputStream == null || resource == null || objectStat == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        long contentLength = objectStat.size();
        String disposition = "attachment; filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(contentLength);
        headers.set("Content-Disposition", disposition);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/upload")
    public Result uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("name") String name) {
        try {
            String tag = minioService.uploadFile(bucketName, file.getOriginalFilename(), file);
            final MlModel model = new MlModel(name, tag);
            mlModelMapper.insert(model);
            Map<String, String> body = new HashMap<>();
            body.put("model_url", tag);
            body.put("model_id", String.valueOf(model.getId()));
            body.put("data_url", "raw_demo.csv");
            final ResponseBody responseBody = client.postFormData(ModelAPI.RAW_DETECT, body).body();
            if (responseBody == null) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new Result().code(500).message("模型不符合规范");
            }
            final String string = responseBody.string();
            final ModelResponse res = mapper.readValue(string, ModelResponse.class);
            if (res.getCode() != 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new Result().code(500).message("模型不符合规范");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Result().code(500).message("模型导入有误");
        }
        return new Result().code(200).message("上传成功");
    }
}
