package com.crane.wordformat.restful.dto;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class CoverFormDTO {

  private List<Map> zh;
  
  private List<Map> en;
}
