package com.x.datago.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.x.datago.dao.IUserDao;
import com.x.datago.entity.User;
import com.x.datago.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * (User)表服务实现类
 *
 * @author AD
 * @since 2022-01-11 22:11:45
 */
@Service("userService")
public class UserService extends ServiceImpl<IUserDao, User> implements IUserService {

}

