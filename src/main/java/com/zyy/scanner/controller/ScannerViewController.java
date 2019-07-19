package com.zyy.scanner.controller;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import io.swagger.annotations.ApiOperation;

/**
 * @Author zhangyy
 * @DateTime 2019-07-19 09:24
 * @Description
 */
@Controller
@RequestMapping("/view")
@RestSchema(schemaId = "ScannerViewController")
public class ScannerViewController {

    @ApiOperation(value = "Scanner主页")
    @RequestMapping("/page")
    public ModelAndView view(){
        return new ModelAndView("/webjars/scanner-ui/0.0.1-SNAPSHOT/index.jsp");
    }

}
