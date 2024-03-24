package com.crane.wordformat.restful.service;

import com.crane.wordformat.restful.dto.FormatProcessDTO;
import com.crane.wordformat.restful.mapper.CoverFormMapper;
import com.crane.wordformat.restful.mapper.FormatConfigMapper;
import com.crane.wordformat.restful.mapper.FormattingTaskMapper;
import com.crane.wordformat.restful.socket.WebSocket;
import com.crane.wordformat.restful.utils.MinioClientUtil;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface IndexService {
    void upload(FormatConfigMapper formatConfigMapper,
                FormattingTaskMapper formattingTaskMapper,
                CoverFormMapper coverFormMapper,
                WebSocket webSocket,
                MinioClientUtil minioClientUtil,
                Map<String, Integer> FILE_TYPE,
                MultipartFile multipartFile,
                FormatProcessDTO formatProcessDTO);
}
