package com.x.nacos.controller;

import com.x.nacos.bean.User;
import java.time.OffsetDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Hello Controller
 *
 * @author AD
 * @date 2022/1/8 17:25
 */
@RequestMapping("now")
@Controller
public class NowController {

    @Autowired
    private User user;

    /**
     * 获取当前时间
     *
     * @return String 当前时间
     */
    @PostMapping("time")
    @ResponseBody
    public String time(User user) {
        System.out.println(user);
        return OffsetDateTime.now().toString();
    }
}
