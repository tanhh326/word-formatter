package com.crane.wordformat.restful.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import com.crane.wordformat.formatter.factory.FormatterFactory;
import com.crane.wordformat.formatter.global.FormattingProcessShareVar;
import com.crane.wordformat.restful.dto.FormatProcessDTO;
import com.crane.wordformat.restful.entity.CoverFormPO;
import com.crane.wordformat.restful.entity.FormatConfigPO;
import com.crane.wordformat.restful.entity.FormattingTaskPO;
import com.crane.wordformat.restful.enums.FormattingTaskStatusEnum;
import com.crane.wordformat.restful.global.RestResponse;
import com.crane.wordformat.restful.mapper.CoverFormMapper;
import com.crane.wordformat.restful.mapper.FormatConfigMapper;
import com.crane.wordformat.restful.mapper.FormattingTaskMapper;
import com.crane.wordformat.restful.service.IndexService;
import com.crane.wordformat.restful.socket.WebSocket;
import com.crane.wordformat.restful.socket.msg.FormatTaskMsg;
import com.crane.wordformat.restful.utils.FilePathUtil;
import com.crane.wordformat.restful.utils.MinioClientUtil;
import io.minio.ObjectStat;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import io.minio.errors.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/word-format")
@RequiredArgsConstructor
public class IndexController {

  private final FormatConfigMapper formatConfigMapper;

  private final FormattingTaskMapper formattingTaskMapper;

  private final CoverFormMapper coverFormMapper;

  private final WebSocket webSocket;

  private final MinioClientUtil minioClientUtil;

  @Autowired
  IndexService indexService;

  private final Map<String, Integer> FILE_TYPE = new HashMap() {{
    put("docx", SaveFormat.DOCX);
    put("doc", SaveFormat.DOC);
  }};

  @PostMapping("/formatting")
  public RestResponse upload(@RequestParam("file") MultipartFile multipartFile,
      @RequestPart("data") FormatProcessDTO formatProcessDTO)
      throws Exception {
    indexService.upload(formatConfigMapper,
            formattingTaskMapper,
            coverFormMapper,
            webSocket,
            minioClientUtil,
            FILE_TYPE,
            multipartFile,
            formatProcessDTO);
    return RestResponse.ok();
  }

  @GetMapping("/beat")
  public String beat() {
    return UUID.randomUUID().toString();
  }

  @PostMapping("/retry")
  public RestResponse retry(@RequestBody FormatProcessDTO data) throws Exception {
    if(data == null){
      return RestResponse.error("请确认请求参数是否正确");
    }
    String jsonStr = JSONObject.toJSONString(data);
    JSONObject parse = (JSONObject) JSONObject.parse(jsonStr);
    String originDocPath = parse.getString("originDocPath");
    String originalFilename = parse.getString("originalFilename");
    InputStream inputStream = minioClientUtil.getObject(originDocPath);
    MultipartFile file = new MockMultipartFile(originDocPath.split("/")[4], originalFilename, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", inputStream);
    indexService.upload(formatConfigMapper,
            formattingTaskMapper,
            coverFormMapper,
            webSocket,
            minioClientUtil,
            FILE_TYPE,
            file,
            data);
    return RestResponse.ok();
  }

  @GetMapping("/socket-test/{status}")
  public void socketTest(@PathVariable Integer status) {
    FormattingTaskPO po = new FormattingTaskPO();
    po.setId(UUID.randomUUID().toString());
    po.setStatus(status);
    po.setOriginDoc("测试文档");
    webSocket.sendAllMessage(
        new FormatTaskMsg().setId(po.getId()).setOriginDoc(po.getOriginDoc())
            .setStatus(po.getStatus()));
  }
}
