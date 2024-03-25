package com.crane.wordformat.restful.controller;

import com.alibaba.fastjson.JSONObject;
import com.aspose.words.SaveFormat;
import com.crane.wordformat.restful.dto.FormatProcessDTO;
import com.crane.wordformat.restful.entity.FormattingTaskPO;
import com.crane.wordformat.restful.global.RestResponse;
import com.crane.wordformat.restful.mapper.CoverFormMapper;
import com.crane.wordformat.restful.mapper.FormatConfigMapper;
import com.crane.wordformat.restful.mapper.FormattingTaskMapper;
import com.crane.wordformat.restful.service.IndexService;
import com.crane.wordformat.restful.socket.WebSocket;
import com.crane.wordformat.restful.socket.msg.FormatTaskMsg;
import com.crane.wordformat.restful.utils.MinioClientUtil;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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
  private final Map<String, Integer> FILE_TYPE = new HashMap() {{
    put("docx", SaveFormat.DOCX);
    put("doc", SaveFormat.DOC);
  }};
  @Autowired
  IndexService indexService;

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
    if (data == null) {
      throw new RuntimeException("请确认请求参数是否正确");
    }
    String jsonStr = JSONObject.toJSONString(data);
    JSONObject parse = (JSONObject) JSONObject.parse(jsonStr);
    String originDocPath = parse.getString("originDocPath");
    String originalFilename = parse.getString("originalFilename");
    InputStream inputStream = minioClientUtil.getObject(originDocPath);
    MultipartFile file = new MockMultipartFile(originDocPath.split("/")[4], originalFilename,
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", inputStream);
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
