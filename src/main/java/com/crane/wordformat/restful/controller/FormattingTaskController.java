package com.crane.wordformat.restful.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.crane.wordformat.restful.entity.FormattingTaskPO;
import com.crane.wordformat.restful.mapper.FormattingTaskMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/formatting-task")
public class FormattingTaskController {

  @Autowired
  private FormattingTaskMapper formattingTaskMapper;

  @PostMapping
  public int add(@RequestBody FormattingTaskPO formattingTaskPO) {
    return formattingTaskMapper.insert(formattingTaskPO);
  }

  @DeleteMapping
  public void delete(@RequestBody List<String> ids) {
    formattingTaskMapper.deleteBatchIds(ids);
  }

  @PutMapping
  public int update(@RequestBody FormattingTaskPO formattingTaskPO) {
    return formattingTaskMapper.updateById(formattingTaskPO);
  }

  @GetMapping
  public IPage list(PageDTO pageDTO, FormattingTaskPO formattingTaskPO) {
    return formattingTaskMapper.selectPage(pageDTO, new LambdaQueryWrapper<FormattingTaskPO>()
        .like(StringUtils.hasText(formattingTaskPO.getId()), FormattingTaskPO::getId,
            formattingTaskPO.getId())
        .like(StringUtils.hasText(formattingTaskPO.getOriginDoc()), FormattingTaskPO::getOriginDoc,
            formattingTaskPO.getOriginDoc())
        .eq(formattingTaskPO.getStatus() != null, FormattingTaskPO::getStatus,
            formattingTaskPO.getStatus()));
  }
}
