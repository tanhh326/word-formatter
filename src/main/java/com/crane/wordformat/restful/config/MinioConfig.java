package com.crane.wordformat.restful.config;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

  private String endpoint;
  private String accessKey;
  private String secretKey;
  private Integer port;

  @Bean
  public MinioClient getMinioClient() throws InvalidPortException, InvalidEndpointException {
    MinioClient minioClient = new MinioClient(getEndpoint(), getPort(), getAccessKey(),
        getSecretKey(), false);
    return minioClient;
  }
}
