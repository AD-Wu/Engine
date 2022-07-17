package com.x.bridge.dao.target;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.x.bridge.bean.Target;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author AD
 * @since 2022-01-11 22:11:43
 */
@Mapper
@Repository
public interface ITargetMapper extends BaseMapper<Target> {
}


