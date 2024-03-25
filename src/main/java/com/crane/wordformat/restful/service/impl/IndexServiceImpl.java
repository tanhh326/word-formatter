package com.crane.wordformat.restful.service.impl;

import com.crane.wordformat.restful.mapper.FormattingTaskMapper;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.aspose.words.Document;
import com.crane.wordformat.formatter.factory.FormatterFactory;
import com.crane.wordformat.formatter.global.FormattingProcessShareVar;
import com.crane.wordformat.restful.dto.FormatProcessDTO;
import com.crane.wordformat.restful.entity.CoverFormPO;
import com.crane.wordformat.restful.entity.FormatConfigPO;
import com.crane.wordformat.restful.entity.FormattingTaskPO;
import com.crane.wordformat.restful.enums.FormattingTaskStatusEnum;
import com.crane.wordformat.restful.mapper.CoverFormMapper;
import com.crane.wordformat.restful.mapper.FormatConfigMapper;
import com.crane.wordformat.restful.service.IndexService;
import com.crane.wordformat.restful.socket.WebSocket;
import com.crane.wordformat.restful.socket.msg.FormatTaskMsg;
import com.crane.wordformat.restful.utils.FilePathUtil;
import com.crane.wordformat.restful.utils.MinioClientUtil;
import io.minio.ObjectStat;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class IndexServiceImpl implements IndexService {
    @Override
    public void upload(FormatConfigMapper formatConfigMapper,
                       FormattingTaskMapper formattingTaskMapper,
                       CoverFormMapper coverFormMapper,
                       WebSocket webSocket,
                       MinioClientUtil minioClientUtil,
                       Map<String, Integer> FILE_TYPE,
                       MultipartFile multipartFile,
                       FormatProcessDTO formatProcessDTO) {

        // 主线程只执行插入日志操作
        FormattingTaskPO po = new FormattingTaskPO();
        String originalFilename = multipartFile.getOriginalFilename();
        po.setOriginDoc(originalFilename);
        po.setCreatedTime(LocalDateTime.now());
        po.setStatus(FormattingTaskStatusEnum.PROCESSING.getValue());
        formattingTaskMapper.insert(po);

        CompletableFuture.runAsync(() -> {
            try {
                // 异步获取文件流创建Document对象
                Document studentDocument = new Document(multipartFile.getInputStream());
                String extName = FileUtil.extName(multipartFile.getOriginalFilename());

                String originalFilePath = FilePathUtil.build(extName, "formatting-origin",
                        DateUtil.format(DateTime.now(), "yyyy/MM/dd"),
                        UUID.randomUUID().toString());
                ByteArrayOutputStream originOutputStream = new ByteArrayOutputStream();
                studentDocument.save(originOutputStream, FILE_TYPE.get(extName));
                minioClientUtil.putObject(originalFilePath,
                        new ByteArrayInputStream(originOutputStream.toByteArray()));
                formatProcessDTO.setOriginDocPath(originalFilePath);
                formatProcessDTO.setOriginalFilename(originalFilename);
                JSONObject formatConfigPOJSON = JSONObject.parseObject(
                        JSONObject.toJSONString(formatProcessDTO));
                po.setRequestParams(formatConfigPOJSON);
                FormatConfigPO formatConfigPO = formatConfigMapper.selectById(
                        formatProcessDTO.getFormatConfigId());

                formatProcessDTO.setInstructionsDissertationAuthorizationDoc(
                        new Document(minioClientUtil.getObject(formatProcessDTO.getDegree().getPath())));

                CoverFormPO zhCoverFormPO = coverFormMapper.selectById(
                        formatProcessDTO.getZhCover().getId());
                formatProcessDTO.getZhCover()
                        .setDocument(
                                new Document(minioClientUtil.getObject(zhCoverFormPO.getCoverTemplateUrl())))
                        .setCoverFormPO(zhCoverFormPO);

                CoverFormPO enCoverFormPO = coverFormMapper.selectById(
                        formatProcessDTO.getEnCover().getId());
                formatProcessDTO.getEnCover()
                        .setDocument(
                                new Document(minioClientUtil.getObject(enCoverFormPO.getCoverTemplateUrl())))
                        .setCoverFormPO(enCoverFormPO);

                po.setFormatConfigName(formatConfigPO.getName());

                FormattingProcessShareVar formattingProcessShareVar = new FormattingProcessShareVar(
                        studentDocument);
                formattingProcessShareVar.setFormatConfigPO(formatConfigPO);
                formattingProcessShareVar.setFormatProcessDTO(formatProcessDTO);
                FormatterFactory.excute(formattingProcessShareVar);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                studentDocument.save(outputStream,
                        FILE_TYPE.get(extName));

                ObjectStat objectStat = minioClientUtil.putObject(
                        FilePathUtil.build(extName, "formatted-result",
                                DateUtil.format(DateTime.now(), "yyyy/MM/dd"),
                                UUID.randomUUID().toString()),
                        new ByteArrayInputStream(outputStream.toByteArray()));
                po.setResultDoc(objectStat.name());
                po.setResultDocSize((objectStat.length() / 1024.0 / 1024.0));
                po.setTotalTimeSpent(
                        Duration.between(po.getCreatedTime(), LocalDateTime.now()).getSeconds());
                po.setStatus(FormattingTaskStatusEnum.SUCCESS.getValue());
            } catch (Exception e) {
                e.printStackTrace();
                po.setErrorMsg(ExceptionUtil.stacktraceToString(e));
                po.setStatus(FormattingTaskStatusEnum.FAIL.getValue());
            } finally {
                formattingTaskMapper.updateById(po);
                webSocket.sendAllMessage(
                        new FormatTaskMsg().setId(po.getId()).setOriginDoc(po.getOriginDoc())
                                .setStatus(po.getStatus()));
            }
        });
    }
}
