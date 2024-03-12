package com.crane.wordformat.restful.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins("*")
        .allowedMethods("POST", "PUT", "GET", "OPTIONS", "DELETE")
        .exposedHeaders("Authorization", "Content-Type", "Accept", "x-requested-with",
            "Cache-Control");
  }
}
