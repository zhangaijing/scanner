package com.zyy.scanner.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Author zhangyy
 * @DateTime 2019-07-18 17:28
 * @Description
 */
@RestController
@RequestMapping("")
public class ScannerPageController {

    @RequestMapping("/scanner.html")
    public ModelAndView test(){
        return new ModelAndView("/webjars/scanner-ui/0.0.1-SNAPSHOT/index.jsp");
    }
}
