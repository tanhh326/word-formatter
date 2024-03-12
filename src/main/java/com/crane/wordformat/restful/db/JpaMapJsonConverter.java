package com.crane.wordformat.restful.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.HashMap;


@Converter
public class JpaMapJsonConverter implements AttributeConverter<HashMap<String, Object>, String> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(HashMap<String, Object> attribute) {
    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public HashMap<String, Object> convertToEntityAttribute(String dbData) {
    try {
      return objectMapper.readValue(dbData, HashMap.class);
    } catch (Exception e) {
      return new HashMap<>();
    }
  }
}
