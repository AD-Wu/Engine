package com.x.bridge.dao.target;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.x.bridge.bean.Target;
import org.springframework.stereotype.Service;

/**
 * @author AD
 * @since 2022-01-11 22:11:45
 */
@Service
public class TargetDao extends ServiceImpl<ITargetMapper, Target> implements ITargetDao {

}

