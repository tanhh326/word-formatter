package com.crane.wordformat.restful.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.crane.wordformat.restful.entity.FormatConfigPO;
import com.crane.wordformat.restful.mapper.FormatConfigMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/format-config")
public class FormatConfigController {

  @Autowired
  private FormatConfigMapper formatConfigMapper;

  @PostMapping
  public int add(@RequestBody FormatConfigPO formatConfigPO) {
    return formatConfigMapper.insert(formatConfigPO);
  }

  @DeleteMapping
  public void delete(@RequestBody List<String> ids) {
    formatConfigMapper.deleteBatchIds(ids);
  }

  @PutMapping
  public int update(@RequestBody FormatConfigPO formatConfigPO) {
    return formatConfigMapper.updateById(formatConfigPO);
  }

  @GetMapping
  public IPage list(PageDTO pageDTO, FormatConfigPO formatConfigPO) {
    return formatConfigMapper.selectPage(pageDTO, new QueryWrapper<>());
  }
}
