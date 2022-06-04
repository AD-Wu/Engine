package com.x.datago.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.x.datago.entity.User;
import com.x.datago.service.IUserService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author AD
 * @date 2022/1/11 14:43
 */
@SpringBootTest
public class IUserDaoTest {

    @Autowired
    private IUserDao userDao;

    @Autowired
    private IUserService userService;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userDao.selectList(null);
        userList.forEach(System.out::println);
        LambdaQueryWrapper<User> where = new LambdaQueryWrapper<>();
        where.eq(User::getAge,18);
        List<User> users = userDao.selectList(where);
        System.out.println("-----------------");
        System.out.println(users);
    }

    @Test
    public void testServiceSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userService.list();
        userList.forEach(System.out::println);
        System.out.println("-----------------");
        LambdaQueryWrapper<User> where = new LambdaQueryWrapper<>();
        where.eq(User::getAge,18);
        List<User> users = userService.list(where);
        System.out.println(users);
        System.out.println("-----------------");
        LambdaQueryChainWrapper<User> query = new LambdaQueryChainWrapper<>(userService.getBaseMapper());
        List<User> list = query.ge(User::getAge, 3).list();
        System.out.println(list);
        System.out.println("-----------------");
        Page<User> page = new Page<>();
        page.setCurrent(0);
        page.setSize(10);
        where.clear();
        where.ge(User::getAge,3);
        Page<User> result = userService.page(page, where);
        List<User> records = result.getRecords();
        System.out.println(records);

    }
}
