package com.crane.wordformat.restful.service;

import com.crane.wordformat.restful.dto.FormatProcessDTO;
import org.springframework.web.multipart.MultipartFile;

public interface IndexService {

  void upload(
      MultipartFile multipartFile,
      FormatProcessDTO formatProcessDTO) throws Exception;
}
