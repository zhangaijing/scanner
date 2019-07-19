package com.zyy.scanner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import io.swagger.annotations.ApiOperation;

/**
 * @Author zhangyy
 * @DateTime 2019-07-19 09:24
 * @Description
 */
@Controller
@RequestMapping("/view")
public class ScannerViewController {


    @ApiOperation(value="测试")
    @PostMapping("/test")
    @ResponseBody
    public String testScan(){
        return "能扫描的到";
    }
}
