package com.zyy.scanner.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

/**
 * @Author zhangyy
 * @DateTime 2019-01-26 17:38
 * @Description
 */
public class RapUtil {

    private static final int NUM = 2;

    private static final String SET = "set";

    /**
     * 使用事例
     *
     * @throws Exception
     */
    public static String fillData(Class beanClass,Map<String,String> genericMap) throws Exception {
        Object fillObj=fillBeanData(beanClass,genericMap);
        String jsonStr= JsonUtils.toJSONString(fillObj);
        return jsonStr;
    }

    /**
     * 获取父类的属性方法
     *
     * @param childClass
     * @return
     * @throws Exception
     */
    private static List<Method> getParentField(Class childClass) throws Exception {
        List<Method> methodList = new ArrayList<>();
        Class tempClass = childClass;
        String classTypeName=tempClass.getTypeName();
        Boolean flag=true;
        if(classTypeName.equals("java.lang.Object")){
            flag=false;
        }
        while (tempClass != null && flag) {
            List<Method> parentMethod=Arrays.asList(tempClass.getDeclaredMethods());
            methodList.addAll(parentMethod);
            tempClass = tempClass.getSuperclass();
            classTypeName=tempClass.getTypeName();
            if(classTypeName.equals("java.lang.Object")){
                flag=false;
            }
        }
        if(CollectionUtils.isNotEmpty(methodList)){
            //过滤掉get的方法
            methodList=methodList.stream().filter(t->t.getName().startsWith("set")).collect(Collectors.toList());
        }
        return methodList;
    }

    /**
     * 自动填充实体类的方法
     *
     * @param beanClass 实体类的class
     * @throws Exception
     */
    private static Object fillBeanData(Class beanClass,Map<String,String> genericMap) throws Exception {
        Object obj = beanClass.newInstance();
        fillBeanDataComm(beanClass, obj,genericMap);
        return obj;
    }

    /**
     * 填充数据重载---调用者提供对象实例
     *
     * @param beanClass
     * @param beanObj
     * @throws Exception
     */
    private static void fillBeanData(Class beanClass, Object beanObj,Map<String,String> genericMap) throws Exception {
        fillBeanDataComm(beanClass, beanObj,genericMap);
    }

    /**
     * 填充数据公用方法
     *
     * @param beanClass
     * @param beanObj
     * @throws Exception
     */
    private static void fillBeanDataComm(Class beanClass, Object beanObj,Map<String,String> genericMap) throws Exception {
        //Method[] methods=beanClass.getDeclaredMethods()
        List<Method> allMethodList = getParentField(beanClass);
        for (Method method : allMethodList) {
            String methodName = method.getName();
            if (methodName.startsWith("set")) {
                Class fieldClass = method.getParameterTypes()[0];
                setFieldVal(beanClass, fieldClass, method, beanObj,genericMap);
            }
        }
    }

    /**
     * 属性类型判断
     *
     * @param beanClass
     * @param fieldClass
     * @param method
     * @param beanObj
     * @throws Exception
     */
    private static void setFieldVal(Class beanClass, Class fieldClass, Method method, Object beanObj,Map<String,String> genericMap) throws Exception {
        Object fillData=oftenTypeFillData(fieldClass);
        if(fillData!=null){
            method.invoke(beanObj,fillData);
        }else if (fieldClass.equals(List.class)) {
            String fieldName=methodNameToFieldName(method.getName());
            Class tempClass = beanClass;
            Field field=getSuperFieldByFieldName(tempClass,fieldName);
            if(field==null){
                return;
            }
            Type type = field.getGenericType();
            listOperation(beanClass,beanObj,method,type,genericMap);
        }else if(fieldClass.getTypeName().equals("int[]")){
            int[] tempInt={1,2};
            method.invoke(beanObj,tempInt);
        } else if(genericMap!=null&&genericMap.size()>0){
            //泛型处理
            String classType=beanClass.getTypeName();
            String genericClass=genericMap.get(classType);
            if(genericClass!=null){
                Class genericClazz=ResolveClassUtil.getClassByPath(genericClass);
                if(genericClazz.equals(List.class)){
                    String fieldName=methodNameToFieldName(method.getName());
                    Class tempClass = beanClass;
                    Field field=getSuperFieldByFieldName(tempClass,fieldName);
                    listOperation(genericClazz,beanObj,method,field.getGenericType(),genericMap);
                }else{
                    Object genericObj=genericClazz.newInstance();
                    fillBeanDataComm(genericClazz,genericObj,genericMap);
                    method.invoke(beanObj,genericObj);
                }
            }
        }else {//自定义类
            /*  字段的泛型判断
            Type type=fieldClass.getGenericSuperclass();
            String fieldName=methodNameToFieldName(method.getName());
            Field tempField=beanClass.getDeclaredField(fieldName);
            tempField.getGenericType().getTypeName();
            */
            Object childBeanObj = fieldClass.newInstance();
            Method[] childClassMethods = fieldClass.getDeclaredMethods();
            for (Method childClassMethod : childClassMethods) {
                String childClassMethodName = childClassMethod.getName();
                if (childClassMethodName.startsWith("set")) {
                    Class childClassFieldClass = childClassMethod.getParameterTypes()[0];
                    setFieldVal(fieldClass, childClassFieldClass, childClassMethod, childBeanObj,genericMap);
                    method.invoke(beanObj, childBeanObj);
                }
            }
        }
    }

    /**
     * List操作
     * @param beanClass
     * @param beanObj
     * @param method
     * @param genericMap
     * @throws Exception
     */
    private static void listOperation(Class beanClass,Object beanObj,Method method,Type type,Map<String,String> genericMap) throws Exception{
        if (type != null) {
            Class listObjClass;
            if(type.getTypeName().equals("T")){
                String genericTypePath=genericMap.get(beanClass.getTypeName());
                listObjClass=ResolveClassUtil.getClassByPath(genericTypePath);
            }else{
                ParameterizedType pt = (ParameterizedType) type;
                if(pt.getActualTypeArguments()[0] instanceof TypeVariable){
                    String genericTypePath=genericMap.get(beanClass.getTypeName());
                    listObjClass=ResolveClassUtil.getClassByPath(genericTypePath);
                }else{
                    listObjClass = (Class) pt.getActualTypeArguments()[0];
                }
            }
            List<Object> objList = new ArrayList<>();
            for (int i = 0; i < NUM; i++) {
                Object listObj = fillListData(listObjClass);
                objList.add(listObj);
            }
            method.invoke(beanObj, objList);
        }
    }

    /**
     * 根据字段名获取本类和父类的字段对象
     * @param tempClass
     * @param fieldName
     * @return
     */
    private static Field getSuperFieldByFieldName(Class tempClass,String fieldName){
        Field field=getFieldByFieldName(tempClass,fieldName);
        if (field == null) {
            String classTypeName;
            Boolean flag=true;
            while (tempClass != null && flag) {
                tempClass = tempClass.getSuperclass();
                classTypeName=tempClass.getTypeName();
                if(classTypeName.equals("java.lang.Object")){
                    flag=false;
                }
                field=getFieldByFieldName(tempClass,fieldName);
                if(field!=null){
                    break;
                }
            }
        }
        return field;
    }

    /**
     * 根据字段名获取字段对象
     * @param beanClass
     * @param fieldName
     * @return
     */
    private static Field getFieldByFieldName(Class beanClass,String fieldName){
        Field field=null;
        try{
            field = beanClass.getDeclaredField(fieldName);
        }catch(NoSuchFieldException e){}
        return field;
    }

    /**
     * list中填充数据
     *
     * @param listParamClass
     */
    private static Object fillListData(Class listParamClass) throws Exception {
        Object listParamObj=oftenTypeFillData(listParamClass);
         if(listParamObj==null){
            listParamObj = listParamClass.newInstance();
            fillBeanData(listParamClass, listParamObj,null);
        }
        return listParamObj;
    }

    /**
     * 常用类型的数据填充
     * @param classParam
     * @return
     */
    private static Object oftenTypeFillData(Class classParam){
        Object listParamObj=null;
        if (classParam.equals(String.class)) {
            listParamObj="string";
        } else if (classParam.equals(Integer.class) || classParam.equals(int.class)) {
            listParamObj = 10;
        } else if (classParam.equals(Long.class) || classParam.equals(long.class)) {
            listParamObj = 99L;
        } else if (classParam.equals(Float.class) || classParam.equals(float.class)) {
            listParamObj = 22.22F;
        } else if (classParam.equals(Double.class) || classParam.equals(double.class)) {
            listParamObj = 66.66;
        } else if (classParam.equals(Date.class)) {
            listParamObj = DateUtil.getSysCurDateTime();
        } else if (classParam.equals(Boolean.class)||classParam.equals(boolean.class)) {
            listParamObj = true;
        } else if (classParam.equals(Byte.class)||classParam.equals(byte.class)) {
            listParamObj = new Byte("1");
        }
        return listParamObj;
    }

    /**
     * 方法名转换为字段名
     *
     * @param methodName
     * @return
     */
    private static String methodNameToFieldName(String methodName) {
        String fieldName = methodName;
        if (methodName.startsWith(SET)) {
            fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
        }
        return fieldName;
    }

}
