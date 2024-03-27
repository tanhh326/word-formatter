package com.crane.wordformat.restful.system.controller;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.GifCaptcha;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crane.wordformat.restful.system.mapper.SysUserMapper;
import com.crane.wordformat.restful.system.po.SysUserPO;
import com.crane.wordformat.restful.system.req.SysUserLogin;
import com.crane.wordformat.restful.system.resp.Captcha;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys-user")
@AllArgsConstructor
public class SysUserController {

  private final SysUserMapper sysUserMapper;

  private final RedisTemplate redisTemplate;

  private final String salt = "{>?:!@#$%^&*()_+:CRANE<>?";


  @PostMapping("/login")
  public void login(@RequestBody SysUserLogin sysUserLogin) {
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
    GifCaptcha captcha = CaptchaUtil.createGifCaptcha(200, 100, 4);
    captcha.getCode();
    redisTemplate.opsForValue().set("", "", 2, TimeUnit.MINUTES);
    return new Captcha().setUuid(UUID.randomUUID().toString())
        .setImg(captcha.getImage().toString());
  }
}
