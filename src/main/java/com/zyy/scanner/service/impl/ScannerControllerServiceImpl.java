package com.zyy.scanner.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.zyy.scanner.constant.CommonConstant;
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

    @Override public PageInitVO getController(String search) throws Exception{
        final String searchContent=search;
        List<ControllerVO> controllerList=new ArrayList<>();
        Set<Class<?>> classList = ResolveClassUtil.getPackageController(CommonConstant.SCANNER_BASE_PAGE, true);
        for(Class clazz:classList){
            ControllerVO controller=ResolveClassUtil.getController(clazz);
            controllerList.add(controller);
        }
        PageInitVO  pageInit=new PageInitVO();
        pageInit.setServiceName(ResolveClassUtil.getProjectName());
        if(!StringUtils.isEmpty(search)){
            controllerList=controllerList.stream().filter(a->{
                if(a.getUrl().indexOf(searchContent)>-1||a.getAuthor().indexOf(searchContent)>-1){
                    return true;
                }else{
                    return false;
                }
            }).collect(Collectors.toList());
        }
        //先按照作者排序再按照URL排序
        controllerList=controllerList.stream().sorted(Comparator.comparing(ControllerVO::getAuthor).thenComparing(ControllerVO::getUrl)).collect(Collectors.toList());
        pageInit.setControllerList(controllerList);
        return pageInit;
    }

    @Override public List<ControllerMethodVO> getControllerMethod(String classPath) throws Exception{
        Class clazz=RapUtilMap.getClassByPath(classPath);
        List<ControllerMethodVO> controllerMethodList=ResolveClassUtil.getControllerMethod(clazz);
        controllerMethodList=controllerMethodList.stream().sorted(Comparator.comparing(ControllerMethodVO::getUrl)).collect(Collectors.toList());
        return controllerMethodList;
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
