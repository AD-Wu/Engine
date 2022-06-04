package com.x.isearch.web.controller;

import java.time.OffsetDateTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author AD
 * @date 2022/4/17 14:03
 */
@RequestMapping("test")
@RestController
public class TestApi {

    @GetMapping("now")
    public String now(){
        return OffsetDateTime.now().toString();
    }
}
