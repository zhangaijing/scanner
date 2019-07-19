package com.zyy.scanner.controller;

import javax.annotation.Resource;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zyy.scanner.model.ControllerMethodParamVO;
import com.zyy.scanner.model.ControllerMethodVO;
import com.zyy.scanner.model.ControllerVO;
import com.zyy.scanner.model.PageInitVO;
import com.zyy.scanner.service.IScannerControllerService;
import io.swagger.annotations.ApiOperation;

/**
 * @Author zhangyy
 * @DateTime 2019-07-16 11:42
 * @Description 包扫描
 */
@RestController
@RequestMapping("/scanner")
public class ScannerController {

    @Resource IScannerControllerService scannerControllerService;

    @ApiOperation(value = "获取所有Controller")
    @PostMapping("/getController")
    public PageInitVO getController() throws Exception{
        return scannerControllerService.getController();
    }

    @ApiOperation(value="获取Controller下的所有方法")
    @PostMapping("/getControllerMethod")
    public List<ControllerMethodVO> getControllerMethod(String classPath) throws Exception{
        return scannerControllerService.getControllerMethod(classPath);
    }

    @ApiOperation(value="获取方法的出入参Json")
    @PostMapping("/getMethodParam")
    public ControllerMethodParamVO getMethodParam(String methodUrl) throws Exception{
        return scannerControllerService.getMethodParam(methodUrl);
    }
}
