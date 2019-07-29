package com.zyy.scanner.model;

import java.lang.reflect.Method;
import java.util.List;

import lombok.Data;

/**
 * @Author zhangyy
 * @DateTime 2019-07-29 14:30
 * @Description
 */
@Data
public class ClassAndParentClassBO {

    /*** 类和父类路径集合 */
    private List<String> classPathList;

    /*** 类和父类方法集合 */
    private List<Method> methodList;
}
