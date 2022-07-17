package com.x.bridge.dao.proxy;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.x.bridge.bean.Proxy;
import org.springframework.stereotype.Service;

/**
 * @author AD
 * @since 2022-01-11 22:11:45
 */
@Service
public class ProxyDao extends ServiceImpl<IProxyMapper, Proxy> implements IProxyDao {

}

