package com.x.datago.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.x.datago.dao.IUserDao;
import com.x.datago.entity.User;
import com.x.datago.service.IUserService;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * (User)表控制层
 *
 * @author AD
 * @since 2022-01-11 22:12:43
 */
@RestController
@RequestMapping("user")
public class UserController {

    /**
     * 服务对象
     */
    @Autowired
    private IUserDao userDao;

    @Autowired
    private IUserService userService;

    @GetMapping("now")
    public String now(){
        ServiceImpl service = new ServiceImpl<>();
        LambdaQueryWrapper<User> where = new LambdaQueryWrapper();
        where.eq(User::getId,1);
        List list = service.list(where);
        System.out.println("list="+list);
        String s = userDao.toString();
        System.out.println(s);
        String s1 = userService.toString();
        System.out.println(s1);
        return OffsetDateTime.now().toString();
    }
}

