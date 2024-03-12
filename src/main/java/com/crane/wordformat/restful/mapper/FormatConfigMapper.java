package com.crane.wordformat.restful.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crane.wordformat.restful.entity.FormatConfigPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface FormatConfigMapper extends BaseMapper<FormatConfigPO> {

}
