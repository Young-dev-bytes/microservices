package com.in28minutes.microservices.mlagenteval.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.in28minutes.microservices.mlagenteval.dao.entity.AgentEvalJobDo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AgentEvalJobMapper extends BaseMapper<AgentEvalJobDo> {
}
