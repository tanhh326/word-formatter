package com.crane.wordformat.restful.controller;

import cn.hutool.core.io.FileUtil;
import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import com.crane.wordformat.restful.global.RestResponse;
import com.crane.wordformat.restful.resp.UploadFileResp;
import com.crane.wordformat.restful.utils.FilePathUtil;
import com.crane.wordformat.restful.utils.MinioClientUtil;
import io.minio.ObjectStat;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

  private final MinioClientUtil minioClientUtil;

  @GetMapping("/download")
  public void download(@RequestParam String path, HttpServletResponse httpServletResponse) {
    minioClientUtil.getObject(path, httpServletResponse);
  }

  @GetMapping("/getPictureURL")
  public RestResponse getPictureURL(@RequestParam String filePaths) throws Exception {
    List<String> list = new ArrayList<>();
    for (String path : filePaths.split(",")) {
      String url = minioClientUtil.getObjectUrl(path);
      list.add(url);
    }
    return RestResponse.ok(list);
  }

  @PostMapping("/upload")
  public UploadFileResp upload(String prefix, @RequestParam("file") MultipartFile multipartFile)
      throws Exception {
    String path = FilePathUtil.build(FileUtil.extName(multipartFile.getOriginalFilename()), prefix,
        UUID.randomUUID().toString());
    ObjectStat objectStat = minioClientUtil.putObject(path, multipartFile.getInputStream());
    return new UploadFileResp().setPath(objectStat.name())
        .setLength(objectStat.length())
        .setCreatedTime(objectStat.createdTime().toLocalDateTime());
  }

  @GetMapping("/doc2pdf")
  public ResponseEntity<byte[]> doc2pdf(@RequestParam String path)
      throws Exception {
    InputStream inputStream = minioClientUtil.getObject(path);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    new Document(inputStream).save(byteArrayOutputStream,
        SaveFormat.PDF);
    return ResponseEntity.ok(byteArrayOutputStream.toByteArray());
  }
}
