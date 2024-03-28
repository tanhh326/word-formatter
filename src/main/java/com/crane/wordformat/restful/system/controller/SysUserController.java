package com.crane.wordformat.restful.system.controller;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.crane.wordformat.restful.system.mapper.SysUserMapper;
import com.crane.wordformat.restful.system.po.SysUserPO;
import com.crane.wordformat.restful.system.req.SysUserLogin;
import com.crane.wordformat.restful.system.resp.Captcha;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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
@RequestMapping("/api/sys-user")
@AllArgsConstructor
public class SysUserController {

  private final SysUserMapper sysUserMapper;

  private final RedisTemplate redisTemplate;

  private final String salt = "{>?:!@#$%^&*()_+:CRANE<>?";

  @PostMapping("/login")
  public void login(@RequestBody SysUserLogin sysUserLogin) {
    Object o = redisTemplate.opsForHash().get("auth:captcha", sysUserLogin.getUuid());
    if (!o.equals(sysUserLogin.getCode())) {
      throw new RuntimeException("验证码错误");
    }
    SysUserPO sysUserPO = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUserPO>().allEq(
        Map.of(SysUserPO::getUsername, sysUserLogin.getUsername(),
            SysUserPO::getPassword, SecureUtil.md5(sysUserLogin.getPassword()) + salt)));
    if (sysUserPO == null) {
      throw new RuntimeException("用户名或密码错误");
    }
    StpUtil.login(sysUserPO.getId());
  }

  @GetMapping("/captcha")
  public Captcha captcha() {
    LineCaptcha captcha = CaptchaUtil.createLineCaptcha(130, 40, 4, 4);
    String uuid = UUID.randomUUID().toString();
    redisTemplate.opsForHash().put("auth:captcha", uuid, captcha.getCode());
    return new Captcha().setUuid(uuid)
        .setImg(captcha.getImageBase64Data());
  }

  @PostMapping
  public int add(@RequestBody SysUserPO sysUserPO) throws Exception {
    return sysUserMapper.insert(sysUserPO);
  }

  @DeleteMapping
  public void delete(@RequestBody List<String> ids) {
    sysUserMapper.deleteBatchIds(ids);
  }

  @PutMapping
  public int update(@RequestBody SysUserPO sysUserPO) throws Exception {
    return sysUserMapper.updateById(sysUserPO);
  }

  @GetMapping
  public PageDTO<SysUserPO> list(PageDTO<SysUserPO> pageDTO, SysUserPO sysUserPO) {
    return sysUserMapper.selectPage(pageDTO,
        new LambdaQueryWrapper<SysUserPO>().like(StringUtils.hasText(sysUserPO.getUsername()),
            SysUserPO::getUsername, sysUserPO.getUsername()));
  }

  @GetMapping("{id}")
  public SysUserPO getById(@PathVariable String id) {
    return sysUserMapper.selectById(id);
  }
}
