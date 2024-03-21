package com.crane.wordformat.restful.controller;

import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import com.crane.wordformat.formatter.factory.FormatterFactory;
import com.crane.wordformat.formatter.global.FormattingProcessShareVar;
import com.crane.wordformat.restful.dto.FormatProcessDTO;
import com.crane.wordformat.restful.entity.FormatConfigPO;
import com.crane.wordformat.restful.entity.FormattingTaskPO;
import com.crane.wordformat.restful.enums.FormattingTaskStatusEnum;
import com.crane.wordformat.restful.global.RestResponse;
import com.crane.wordformat.restful.mapper.CoverFormMapper;
import com.crane.wordformat.restful.mapper.FormatConfigMapper;
import com.crane.wordformat.restful.mapper.FormattingTaskMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

  @PostMapping("/formatting")
  public RestResponse upload(@RequestParam("file") MultipartFile multipartFile,
      @RequestPart("data") FormatProcessDTO formatProcessDTO)
      throws Exception {
    FormatConfigPO formatConfigPO = formatConfigMapper.selectById(
        formatProcessDTO.getFormatConfigId());
    formatProcessDTO.getZhCover()
        .setCoverFormPO(coverFormMapper.selectById(formatProcessDTO.getZhCover().getId()));
    formatProcessDTO.getEnCover()
        .setCoverFormPO(coverFormMapper.selectById(formatProcessDTO.getEnCover().getId()));
    FormattingTaskPO po = new FormattingTaskPO();
    po.setOriginDoc(multipartFile.getOriginalFilename());
    po.setFormatConfigName(formatConfigPO.getName());
    po.setCreatedTime(LocalDateTime.now());
    po.setStatus(FormattingTaskStatusEnum.PROCESSING.getValue());
    formattingTaskMapper.insert(po);
    Document studentDocument = new Document(multipartFile.getInputStream());

    CompletableFuture.runAsync(() -> {
      try {
        FormattingProcessShareVar formattingProcessShareVar = new FormattingProcessShareVar(
            studentDocument);
        formattingProcessShareVar.setFormatConfigPO(formatConfigPO);
        formattingProcessShareVar.setFormatProcessDTO(formatProcessDTO);
        FormatterFactory.excute(formattingProcessShareVar);
        String path =
            "D:\\work\\word-formatter\\storage-files\\result\\" + UUID.randomUUID()
                + ".docx";
        studentDocument.save(path, SaveFormat.DOCX);
        po.setResultDoc(path);
        po.setTotalTimeSpent(
            Duration.between(po.getCreatedTime(), LocalDateTime.now()).getSeconds());
        po.setStatus(FormattingTaskStatusEnum.SUCCESS.getValue());
      } catch (Exception e) {
        e.printStackTrace();
        // todo 需要改成保存完整堆栈日志
        po.setErrorMsg(e.getMessage());
        po.setStatus(FormattingTaskStatusEnum.FAIL.getValue());
      } finally {
        formattingTaskMapper.updateById(po);
      }
    });
    return RestResponse.ok();
  }

  @GetMapping("/beat")
  public String beat() {
    return UUID.randomUUID().toString();
  }
}
