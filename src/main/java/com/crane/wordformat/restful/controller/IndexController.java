package com.crane.wordformat.restful.controller;

import com.alibaba.fastjson.JSONObject;
import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import com.aspose.words.SectionCollection;
import com.crane.wordformat.restful.dto.CoverFormDTO;
import com.crane.wordformat.restful.dto.FormatProcessDTO;
import com.crane.wordformat.restful.entity.FormattingTaskPO;
import com.crane.wordformat.restful.global.RestResponse;
import com.crane.wordformat.restful.service.IndexService;
import com.crane.wordformat.restful.socket.WebSocket;
import com.crane.wordformat.restful.socket.msg.FormatTaskMsg;
import com.crane.wordformat.restful.utils.MinioClientUtil;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/word-format")
@RequiredArgsConstructor
public class IndexController {

  private final WebSocket webSocket;

  private final MinioClientUtil minioClientUtil;

  private final IndexService indexService;

  @PostMapping("/formatting")
  public RestResponse upload(@RequestParam("file") MultipartFile multipartFile,
      @RequestPart("data") FormatProcessDTO formatProcessDTO)
      throws Exception {
    indexService.upload(multipartFile, formatProcessDTO);
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
    indexService.upload(file, data);
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

  @PostMapping("/analyse-cover")
  public RestResponse<Map> analyseCover(@RequestParam("file") MultipartFile multipartFile,
      @RequestPart("data") CoverFormDTO coverFormDTO) throws Exception {
    SectionCollection sections = new Document(multipartFile.getInputStream()).getSections();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json;charset=UTF-8");

    LinkedHashMap zhVal = new LinkedHashMap();
    LinkedHashMap enVal = new LinkedHashMap();
    RestTemplate restTemplate = new RestTemplate();

    try {
      ResponseEntity<LinkedHashMap> response = restTemplate.postForEntity(
          "http://139.159.190.234:5000/analyse",
          new HttpEntity<>(Map.of("text", sections.get(0).toString(SaveFormat.TEXT),
              "empty_json_list", coverFormDTO.getZh()), headers), LinkedHashMap.class);

      if (response.getStatusCodeValue() == 200) {
        zhVal = response.getBody();
      }
    } catch (Exception e) {
    }
    try {
      ResponseEntity<LinkedHashMap> response2 = restTemplate.postForEntity(
          "http://139.159.190.234:5000/analyse",
          new HttpEntity<>(Map.of("text", sections.get(2).toString(SaveFormat.TEXT),
              "empty_json_list", coverFormDTO.getEn()), headers), LinkedHashMap.class);
      if (response2.getStatusCodeValue() == 200) {
        enVal = response2.getBody();
      }
    } catch (Exception e) {
    }
    return RestResponse.ok(Map.of("zh", zhVal, "en", enVal));
  }
}
