package com.crane.wordformat.restful.global;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class TheRestControllerAdvice implements ResponseBodyAdvice<Object> {

  @Autowired
  private ObjectMapper objectMapper;

  @ExceptionHandler(Exception.class)
  public RestResponse<?> customException(Exception e) {
    e.printStackTrace();
    return RestResponse.error(e.getMessage());
  }

  @Override
  public boolean supports(MethodParameter returnType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return returnType.getParameterType() != ResponseEntity.class;
  }

  @SneakyThrows
  @Override
  public Object beforeBodyWrite(Object body, MethodParameter returnType,
      MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request, ServerHttpResponse response) {
    if (body instanceof String) {
      throw new RestClientException("不支持返回String类型");
    }
    if (body instanceof RestResponse) {
      return body;
    }
    return RestResponse.ok(body);
  }
}
