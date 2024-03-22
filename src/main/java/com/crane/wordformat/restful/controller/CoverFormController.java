package com.crane.wordformat.restful.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.crane.wordformat.restful.entity.CoverFormPO;
import com.crane.wordformat.restful.mapper.CoverFormMapper;
import com.crane.wordformat.restful.repository.CoverFormRepository;
import com.crane.wordformat.restful.resp.BaseResp;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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

  @Autowired
  private CoverFormRepository coverFormRepository;

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
  public IPage list(PageDTO pageDTO, BaseResp baseResp, Pageable pageable,
      CoverFormPO coverFormPO) {
    pageDTO.addOrder(baseResp.getOrders());
    coverFormRepository.findAll(pageable);
    return coverFormMapper.selectPage(pageDTO, new QueryWrapper<>());
  }

  @GetMapping("{id}")
  public CoverFormPO getById(@PathVariable String id) {
    return coverFormMapper.selectById(id);
  }
}
