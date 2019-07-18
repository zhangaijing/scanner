package com.zyy.scanner.service;

import java.util.List;

import com.zyy.scanner.model.ControllerMethodParamVO;
import com.zyy.scanner.model.ControllerMethodVO;
import com.zyy.scanner.model.ControllerVO;

/**
 * @Author zhangyy
 * @DateTime 2019-07-16 11:57
 * @Description
 */
public interface IScannerControllerService {

    /**
     * controller扫描
     * @return
     */
    List<ControllerVO> getController() throws Exception;

    /**
     * 获取方法
     * @param classPath
     * @return
     * @throws Exception
     */
    List<ControllerMethodVO> getControllerMethod(String classPath) throws Exception;

    /**
     * 根据方法URL获取方法的出入参JSON
     * @param methodUrl
     * @return
     * @throws Exception
     */
    ControllerMethodParamVO getMethodParam(String methodUrl) throws Exception;
}
