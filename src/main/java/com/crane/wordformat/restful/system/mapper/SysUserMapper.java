package com.crane.wordformat.restful.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crane.wordformat.restful.system.po.SysUserPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Repository
@Mapper
public interface SysUserMapper extends BaseMapper<SysUserPO> {


}
