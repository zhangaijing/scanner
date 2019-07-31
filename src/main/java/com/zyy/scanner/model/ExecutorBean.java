package com.zyy.scanner.model;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import lombok.Data;

/**
 * @Author zhangyy
 * @DateTime 2019-07-29 14:30
 * @Description
 */
@Data
public class ExecutorBean implements Serializable {

    /**
     * 方法
     */
    private Method method;

    /**
     * controller类的mapping
     */
    private String classMapping;

    /**
     * 类所在包名
     */
    private String packageName;

    /**
     * 类名
     */
    private String className;

    /**
     * 请求参数名称
     */
    private String paramClassName;

    /**
     * 方法的url
     */
    private String url;

    /**
     * 请求参数类的请求参数
     */
    private List<String> paramNameList;

    /**
     * 请求参数类的请求参数对象
     */
    private List<ParamVO> paramVOList;

}
