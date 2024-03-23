package com.crane.wordformat.restful.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.crane.wordformat.restful.entity.CoverFormPO;
import com.crane.wordformat.restful.mapper.CoverFormMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cover-form")
public class CoverFormController {

  @Autowired
  private CoverFormMapper coverFormMapper;

  @PostMapping
  public int add(@RequestBody CoverFormPO coverFormPO) {
    return coverFormMapper.insert(coverFormPO);
  }

  @DeleteMapping
  public void delete(@RequestBody List<String> ids) {
    coverFormMapper.deleteBatchIds(ids);
  }

  @PutMapping
  public int update(@RequestBody CoverFormPO coverFormPO) {
    return coverFormMapper.updateById(coverFormPO);
  }

  // fixme 最新版本分页有问题，先将就用吧
  @GetMapping
  public PageDTO<CoverFormPO> list(PageDTO<CoverFormPO> pageDTO, CoverFormPO coverFormPO) {
    return coverFormMapper.selectPage(pageDTO,
        new LambdaQueryWrapper<CoverFormPO>().like(StringUtils.hasText(coverFormPO.getName()),
            CoverFormPO::getName, coverFormPO.getName()));
  }

  @GetMapping("{id}")
  public CoverFormPO getById(@PathVariable String id) {
    return coverFormMapper.selectById(id);
  }
}
