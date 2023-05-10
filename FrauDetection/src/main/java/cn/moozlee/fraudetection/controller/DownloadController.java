package cn.moozlee.fraudetection.controller;

import cn.moozlee.fraudetection.entity.MlModel;
import cn.moozlee.fraudetection.service.MinioService;
import io.minio.StatObjectResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author moozlee
 * @create 2023-05-10 14:11
 */
@RestController
public class DownloadController {

    @Autowired
    MinioService minioService;
    @Value("${minio.bucketName}")
    private String bucketName;

    @GetMapping("/file/download/{path}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("path") String path, HttpServletResponse request) throws UnsupportedEncodingException {
        InputStream inputStream = null;
        Resource resource = null;
        StatObjectResponse objectStat = null;
        try {
            inputStream = minioService.downloadFile(bucketName, path);
            resource = new InputStreamResource(inputStream);
            objectStat = minioService.getObjectStat(bucketName, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (inputStream == null || resource == null || objectStat == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        long contentLength = objectStat.size();
        String disposition = "attachment; filename=\"" + URLEncoder.encode(path, "UTF-8") + "\"";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(contentLength);
        headers.set("Content-Disposition", disposition);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
