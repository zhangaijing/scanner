package com.zyy.scanner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import io.swagger.annotations.ApiOperation;

/**
 * @Author zhangyy
 * @DateTime 2019-07-19 09:24
 * @Description
 */
@RestController
@RequestMapping("/view")
public class ScannerViewController {

    @ApiOperation(value = "Scanner主页")
    @RequestMapping(value="/page",method=RequestMethod.GET)
    public ModelAndView view(){
        return new ModelAndView("/webjars/scanner-ui/0.0.1-SNAPSHOT/index.jsp");
    }

    @ApiOperation(value="测试")
    @PostMapping("/test")
    @ResponseBody
    public String testScan(){
        return "能扫描的到";
    }
}
