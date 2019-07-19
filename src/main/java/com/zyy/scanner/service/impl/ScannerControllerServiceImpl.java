package com.zyy.scanner.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import com.zyy.scanner.model.ControllerMethodParamVO;
import com.zyy.scanner.model.ControllerMethodVO;
import com.zyy.scanner.model.ControllerVO;
import com.zyy.scanner.model.PageInitVO;
import com.zyy.scanner.model.ParamVO;
import com.zyy.scanner.service.IScannerControllerService;
import com.zyy.scanner.util.RapUtilMap;
import com.zyy.scanner.util.ResolveClassUtil;

/**
 * @Author zhangyy
 * @DateTime 2019-07-16 11:57
 * @Description
 */
@Service
public class ScannerControllerServiceImpl implements IScannerControllerService {

    @Override public PageInitVO getController() throws Exception{
        List<ControllerVO> controllerList=new ArrayList<>();
        Set<Class<?>> classList = ResolveClassUtil.getPackageController("com.hoolink", true);
        for(Class clazz:classList){
            ControllerVO controller=ResolveClassUtil.getController(clazz);
            controllerList.add(controller);
        }
        PageInitVO  pageInit=new PageInitVO();
        pageInit.setServiceName(ResolveClassUtil.getProjectName());
        pageInit.setControllerList(controllerList);
        return pageInit;
    }

    @Override public List<ControllerMethodVO> getControllerMethod(String classPath) throws Exception{
        Class clazz=RapUtilMap.getClassByPath(classPath);
        return ResolveClassUtil.getControllerMethod(clazz);
    }

    @Override public ControllerMethodParamVO getMethodParam(String methodUrl) throws Exception{
        ControllerMethodVO controllerMethod=ResolveClassUtil.getControllerMethodCache(methodUrl);
        ControllerMethodParamVO controllerMethodParam=new ControllerMethodParamVO();
        if(controllerMethod!=null){
            String returnJson=RapUtilMap.fillReturnAndParamBeanToJson(controllerMethod.getReturnType());
            List<ParamVO> paramList=controllerMethod.getParamList();
            String paramJson=RapUtilMap.muliParamFillBeanToJson(paramList);
            controllerMethodParam.setParamJson(paramJson);
            controllerMethodParam.setReturnJson(returnJson);
            controllerMethodParam.setUrl("/"+ResolveClassUtil.getProjectName()+controllerMethod.getUrl());
        }
        return controllerMethodParam;
    }
}
