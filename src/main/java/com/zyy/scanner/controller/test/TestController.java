package com.zyy.scanner.controller.test;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zyy.scanner.model.PageInitVO;
import io.swagger.annotations.ApiOperation;

/**
 * @Author zhangyy
 * @DateTime 2019-07-26 11:14
 * @Description
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @ApiOperation(value = "获取所有Controller")
    @PostMapping("/testController")
    public Void testController(String search) throws Exception{
        return null;
    }

    @ApiOperation(value = "获取所有Controller")
    @PostMapping("/testController2")
    public Void testController2(String search) throws Exception{
        return null;
    }

    @ApiOperation(value = "获取所有Controller")
    @PostMapping("/testController1")
    public Void testController1(String search) throws Exception{
        return null;
    }
}
