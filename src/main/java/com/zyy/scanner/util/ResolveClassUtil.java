package com.zyy.scanner.util;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.reflections.Reflections;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zyy.scanner.constant.CommonConstant;
import com.zyy.scanner.model.ControllerMethodVO;
import com.zyy.scanner.model.ControllerVO;
import com.zyy.scanner.model.ExecutorBean;
import com.zyy.scanner.model.ParamVO;
import io.swagger.annotations.ApiOperation;

/**
 * @Author zhangyy
 * @DateTime 2019-01-26 17:38
 * @Description 包Controller扫描工具类
 */
public class ResolveClassUtil {

    private static String projectName="";
    private static Map<String,ControllerMethodVO> controllerMethodMap=new HashMap<>();

    static{
        try{
            projectName=CommentReaderUtil.getProjectName();
        }catch (Exception e){

        }
    }

    /**
     * 根据类获取controller的路径、注释、作者
     * @param clazz
     * @return
     */
    public static ControllerVO getController(Class clazz) throws Exception{
        RequestMapping requestMapping=(RequestMapping) clazz.getAnnotation(RequestMapping.class);
        String[] controllerUrlArr=requestMapping.value();
        String controllerUrl=controllerUrlArr[0];
        String classPath=clazz.getTypeName();
        Map<String,String> commentMap=CommentReaderUtil.getClassFieldComment(classPath);
        String classComment=commentMap.get(CommonConstant.KEY_CLASS_COMMENT);
        String lowerComment=classComment.toLowerCase();
        Integer authIndex=lowerComment.indexOf(CommonConstant.AUTHOR);
        Integer descIndex=lowerComment.indexOf(CommonConstant.DESC_FULL);
        String authorComment="";
        String descComment="";
        Integer authLen=CommonConstant.DESC_FULL.length();
        Integer enterIndex;
        if(authIndex>-1){
            enterIndex=lowerComment.indexOf(CommonConstant.ENTER_CHAR,authIndex);
            authorComment=classComment.substring(authIndex+CommonConstant.AUTHOR.length(),enterIndex).trim();
        }
        if(descIndex==-1){
            descIndex=lowerComment.indexOf(CommonConstant.DESC_SHORT);
            authLen=CommonConstant.DESC_SHORT.length();
        }
        if(descIndex>-1){
            enterIndex=lowerComment.indexOf(CommonConstant.ENTER_CHAR,descIndex);
            descComment=classComment.substring(descIndex+authLen,enterIndex).trim();
        }
        ControllerVO controller=new ControllerVO();
        controller.setUrl(controllerUrl);
        controller.setComment(descComment);
        controller.setAuthor(authorComment);
        controller.setClassPath(classPath);
        return controller;
    }

    /**
     * 获取controller方法信息（url，注释，入参，出参）
     * @param classes
     * @return
     */
    public static List<ControllerMethodVO> getControllerMethod(Class classes) throws Exception{
        List<ControllerMethodVO> controllerMethodList=new ArrayList<>();
        Method[] methods = classes.getDeclaredMethods();
        RequestMapping requestMappingClass = (RequestMapping) classes.getAnnotation(RequestMapping.class);
        String controllerUrl;
        try {
            String[] requestMappingClassValue = requestMappingClass.value();
            controllerUrl = requestMappingClassValue[0];
        } catch (Exception e) {
            controllerUrl = "";
        }
        for (Method method : methods) {
            PostMapping requestMapping = method.getAnnotation(PostMapping.class);
            if (null != requestMapping) {
                ControllerMethodVO controllerMethod=new ControllerMethodVO();
                String methodUrl=requestMapping.value()[0];
                controllerMethod.setUrl(controllerUrl+methodUrl);
                ApiOperation apiOperation=method.getAnnotation(ApiOperation.class);
                String methodComment=apiOperation.value();
                controllerMethod.setComment(methodComment);
                String returnClassType=getMethodReturnGenericType(method);
                controllerMethod.setReturnType(returnClassType);
                List<ParamVO> methodParamList=getParamListByMethod(method);
                controllerMethod.setParamList(methodParamList);
                controllerMethodList.add(controllerMethod);
                controllerMethodMap.put(controllerMethod.getUrl(),controllerMethod);
            }
        }
        return controllerMethodList;
    }

    /**
     * 根据方法URL获取方法信息---缓存读取
     * @param methodUrl
     * @return
     */
    public static ControllerMethodVO getControllerMethodCache(String methodUrl){
        return controllerMethodMap.get(methodUrl);
    }

    /**
     * 获取项目名称
     * @return
     */
    public static String getProjectName(){
        return projectName;
    }

    /**
     * 获取requestmapping方法
     * @param classes
     * @return
     */
    public static Map<String,ExecutorBean> getRequestMappingMethod(Class classes) {
        Map<String, ExecutorBean> mapp = new HashMap<String, ExecutorBean>();
        Method[] methods = classes.getDeclaredMethods();
        //RequestMapping requestMappingClass = (RequestMapping) classes.getAnnotation(RequestMapping.class)z
        RequestMapping requestMappingClass = (RequestMapping) classes.getAnnotation(RequestMapping.class);
        String classMappValue;
        try {
            String[] requestMappingClassValue = requestMappingClass.value();
            classMappValue = requestMappingClassValue[0];
        } catch (Exception e) {
            classMappValue = "";
        }
        for (Method method : methods) {
            PostMapping requestMapping = method.getAnnotation(PostMapping.class);
            if (null != requestMapping) {
                ExecutorBean executorBean = new ExecutorBean();
                try {
                    executorBean.setClassMapping(classMappValue);
                    executorBean.setMethod(method);
                    executorBean.setPackageName(classes.
                            getPackage().getName());
                    executorBean.setClassName(classes.getSimpleName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String key = Arrays.toString(requestMapping.value());
                if (StringUtils.isEmpty(key)) {
                    continue;
                }
                executorBean.setMethod(method);
                mapp.put(Arrays.toString(requestMapping.value()).replaceFirst("/", ""), executorBean);
            }
        }
        return sortMapByKey(mapp);
    }

    /**
     * 排序
     * @param map
     * @return
     */
    public static Map<String, ExecutorBean> sortMapByKey(Map<String, ExecutorBean> map) {
        Map<String, ExecutorBean> sortedMap = new TreeMap<String, ExecutorBean>(new Comparator<String>() {
            @Override public int compare(String keyl, String key2) {
                int flag = keyl.compareTo(key2);
                int flag2 = 0;
                if (flag > 0) {
                    flag2 = -1;
                } else if (flag < 0) {
                    flag2 = 1;
                }
                return flag2;
            }
        });
        sortedMap.putAll(map);
        return sortedMap;
    }

    /**
     * 获取包下的controller类（含子包）
     * @param packageName
     * @param subPackageFlag
     * @return
     */
    public static Set<Class<?>> getPackageController(String packageName, boolean subPackageFlag) throws Exception{
        Set<String> packageNametist = new HashSet<String>();
        packageNametist.add(packageName);
        if (subPackageFlag) {
            packageNametist.addAll(getPackageList(packageName));
        }
        Set<Class<?>> classesList = new HashSet<Class<?>>();
        for (String p : packageNametist) {
            classesList.addAll(getPackageController(p));
        }
        return classesList;
    }

    /**
     * 获取当前包下的controller
     * @param packageName
     * @return
     */
    public static Set<Class<?>> getPackageController(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> classeslist = reflections.getTypesAnnotatedWith(RestController.class);
        return classeslist;
    }

    /**
     * 获取方法的参数列表
     * @param method
     * @return
     */
    public static Set<Class<?>> getParamByMethod(Method method) {
        Set<Class<?>> paramNameSet = new HashSet<Class<?>>();
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> clas : parameterTypes) {
            String parameterName = clas.getName();
            if (parameterName.contains("HttpServletRequest")) {
                continue;
            }
            paramNameSet.add(clas);
        }
        return paramNameSet;
    }

    /**
     * 获取方法参数列表---List<ParamVO>形式
     * @param method
     * @return
     */
    public static List<ParamVO> getParamListByMethod(Method method) {
        List<ParamVO> paramList=new ArrayList<>();
        Parameter[] parameterTypes = method.getParameters();
        for (Parameter parameter : parameterTypes) {
            Type paramType=parameter.getParameterizedType();
            String parameterName = parameter.getName();
            Class paramClass=parameter.getType();
            String paramClassStr=paramClass.getName();
            if(paramType instanceof ParameterizedType){
                paramClassStr=paramType.getTypeName();
            }
            ParamVO param=new ParamVO();
            param.setParamName(parameterName);
            param.setParamType(paramClassStr);
            paramList.add(param);
        }
        return paramList;
    }

    /**
     * 获取方法参数列表---MAP形式
     * @param method
     * @return
     */
    public static Map<String,String> getParamMapByMethod(Method method) {
        Map<String,String> paramMap=new HashMap<>();
        Parameter[] parameterTypes = method.getParameters();
        for (Parameter parameter : parameterTypes) {
            String parameterName = parameter.getName();
            Class paramClass=parameter.getType();
            String paramClassStr=paramClass.getName();
            paramMap.put(parameterName,paramClassStr);
        }
        return paramMap;
    }

    /**
     * 获取方法的入参泛型类型
     * @param method
     * @return
     */
    public static List<Type> getMethodParamType(Method method){
        List<Type> typeList=new ArrayList<>();
        Type[] types=method.getGenericParameterTypes();
        for(Type type:types){
            if(type instanceof ParameterizedType){
                Type[] typeArr=((ParameterizedType) type).getActualTypeArguments();
                for(Type typeItem:typeArr){
                    typeList.add(typeItem);
                }
            }
        }
        return typeList;
    }

    /**
     * 获取方法的返回泛型类型
     * @throws NoSuchMethodException
     */
    public static String getMethodReturnGenericType(Method method) throws NoSuchMethodException{
        Type type=method.getGenericReturnType();
        String typeName=null;
        if(type!=null){
            if(type instanceof ParameterizedType){
                typeName=type.getTypeName();
            }else{
                typeName=method.getReturnType().getTypeName();
            }
        }
        return typeName;
    }

    /**
     * 获取controller包下的类文件
     * @param packagePath
     * @param packageNameList
     * @return
     */
    private static Set<String> getPackageByFile(String packagePath, Set<String> packageNameList) {
        if(packagePath.indexOf(CommonConstant.CONTROLLER_CHAR)>0){
            File file = new File(packagePath);
            File[] childFiles = file.listFiles();
            for (
                    File childFile : childFiles) {
                if (childFile.isDirectory()) {
                    packageNameList.add(childFile.getAbsolutePath());
                    getPackageByFile(childFile.getAbsolutePath(), packageNameList);
                }
            }
        }
        return packageNameList;
    }

    /**
     * 获取包下的子包和子包下的类文件
     * @param packageName
     * @return
     */
    public static Set<String> getPackageList(String packageName) {
        Set<String> fileNames = new HashSet<String>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace(".", "/");
        URL url = loader.getResource(packagePath);
        if (url != null) {
            String type = url.getProtocol();
            if (type.equals("file")) {
                getPackageByFile(url.getPath(), fileNames);
            }
        }
        String path = url.getPath().replaceFirst("/", "").replace("/", ".");
        Set<String> filePathSet = new HashSet<String>();
        for (String str : fileNames) {
            str = str.replace("\\", ".");
            String packageEnd = str.replace(path, "");
            filePathSet.add(packageName + packageEnd);
        }
        return filePathSet;
    }

    /**
     * 根据类路径获取类class
     * @param classPath
     * @return
     */
    public static Class getClassByPath(String classPath) throws ClassNotFoundException{
        ClassLoader classLoader=Thread.currentThread().getContextClassLoader();
        Class clazz=classLoader.loadClass(classPath);
        return clazz;
    }

    public static void print(String info, Object... args) {
        System.out.println(String.format(info, args));
    }

}