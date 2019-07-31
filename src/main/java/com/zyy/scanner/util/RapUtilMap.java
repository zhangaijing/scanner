package com.zyy.scanner.util;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.zyy.scanner.constant.CommonConstant;
import com.zyy.scanner.model.ClassAndParentClassBO;
import com.zyy.scanner.model.ParamVO;

/**
 * @Author zhangyy
 * @DateTime 2019-01-26 17:38
 * @Description   填充数据MAP形式
 */
public class RapUtilMap {


    /**
     * 出入参类型数据填充
     * @param classPath
     * @return
     * @throws Exception
     */
    public static String fillReturnAndParamBeanToJson(String classPath) throws Exception{
        Map<String,String> genericMap=resolveGenericClass(classPath);
        String returnJson;
        if(classPath.equals(CommonConstant.TYPE_VOID)){
            return CommonConstant.NUL_STR;
        }
        if(genericMap.size()>0){
            String firstClassPath=genericMap.get(CommonConstant.KEY_RAP_UTIL_FIRST_CLASS);
            Class firstClass=getClassByPath(firstClassPath);
            returnJson=fillData(firstClass,genericMap);
        }else{
            returnJson=fillBeanToJson(classPath);
        }
        return returnJson;
    }

    /**
     * 方法的多个入参数据填充----入参专用
     * @param paramList
     * @return
     * @throws Exception
     */
    public static String muliParamFillBeanToJson(List<ParamVO> paramList) throws Exception{
        String paramJson=CommonConstant.NUL_STR;
        if(CollectionUtils.isNotEmpty(paramList)){
            if(paramList.size()==1){
                String paramClassPath=paramList.get(0).getParamType();
                if(paramClassPath.indexOf(CommonConstant.LEFT_ANGLE_BRACKET)>-1){
                    paramJson=RapUtilMap.fillReturnAndParamBeanToJson(paramClassPath);
                }else{
                    Object paramTypeVal=oftenTypeFillData(paramClassPath);
                    if(paramTypeVal==null){
                        paramJson=RapUtilMap.fillReturnAndParamBeanToJson(paramClassPath);
                    }else{
                        Map<String,Object> paramMap=new HashMap<>();
                        paramMap.put(paramList.get(0).getParamName(),paramTypeVal);
                        paramJson=JsonUtils.toJSONString(paramMap);
                    }
                }
            }else{
                Map<String,Object> paramMap=new HashMap<>();
                for(ParamVO param:paramList){
                    String paramClassPath=param.getParamType();
                    if(paramClassPath.indexOf("<")>-1){
                        paramMap.put(param.getParamName(),JsonUtils.stringToMap(RapUtilMap.fillReturnAndParamBeanToJson(paramClassPath)));
                    }else{
                        Object paramTypeVal=oftenTypeFillData(paramClassPath);
                        if(paramTypeVal==null){
                            paramMap.put(param.getParamName(),JsonUtils.stringToMap(RapUtilMap.fillReturnAndParamBeanToJson(paramClassPath)));
                        }else{
                            paramMap.put(param.getParamName(),paramTypeVal);
                        }
                    }
                }
                paramJson=JsonUtils.toJSONString(paramMap);
            }
        }
        return paramJson;
    }

    /**
     * 出入参数据填充----泛型
     *
     * @throws Exception
     */
    public static String fillData(Class beanClass,Map<String,String> genericMap) throws Exception {
        String jsonStr;
        if(beanClass.equals(List.class)){
            List<Object> listObj=new ArrayList<>();
            String classTypeName=beanClass.getTypeName();
            String listItemType=genericMap.get(classTypeName);
            if(listItemType!=null){
                Class listItemClass=getClassByPath(listItemType);
                for(int i=0;i<CommonConstant.ARR_FILL_MAX_NUM;i++){
                    Object fillObj=fillBeanData(listItemClass,genericMap);
                    listObj.add(fillObj);
                }
                jsonStr=JsonUtils.toJSONString(listObj);
            }else{
                jsonStr=CommonConstant.NUL_STR;
            }
        }else{
            Object fillObj=fillBeanData(beanClass,genericMap);
            jsonStr= JsonUtils.toJSONString(fillObj);
        }
        return jsonStr;
    }

    /**
     * 出入参数据填充----不支持泛型
     * @param classPath
     * @return
     * @throws Exception
     */
    public static String fillBeanToJson(String classPath) throws Exception{
        Class clazz=getClassByPath(classPath);
        String beanJson=fillData(clazz,null);
        return beanJson;
    }

    /**
     * 获取父类的属性方法
     *
     * @param childClass
     * @return
     * @throws Exception
     */
    private static ClassAndParentClassBO getParentField(Class childClass) throws Exception {
        ClassAndParentClassBO classAndParentClass=new ClassAndParentClassBO();
        List<String> classAndParentClassPath=new ArrayList<>();
        List<Method> methodList = new ArrayList<>();
        Class tempClass = childClass;
        String classTypeName=tempClass.getTypeName();
        Boolean flag=true;
        if(classTypeName.equals(CommonConstant.TYPE_OBJECT)){
            flag=false;
        }
        while (tempClass != null && flag) {
            List<Method> parentMethod=Arrays.asList(tempClass.getDeclaredMethods());
            methodList.addAll(parentMethod);
            if(!tempClass.getName().equals(CommonConstant.TYPE_OBJECT)){
                classAndParentClassPath.add(tempClass.getName());
            }
            tempClass = tempClass.getSuperclass();
            //类的继承
            if(tempClass!=null){
                classTypeName=tempClass.getTypeName();
                if(classTypeName.equals(CommonConstant.TYPE_OBJECT)){
                    flag=false;
                }
            }
        }
        if(CollectionUtils.isNotEmpty(methodList)){
            //过滤掉get的方法
            methodList=methodList.stream().filter(t->t.getName().startsWith(CommonConstant.SET_STR)).collect(Collectors.toList());
        }
        classAndParentClass.setClassPathList(classAndParentClassPath);
        classAndParentClass.setMethodList(methodList);
        return classAndParentClass;
    }

    /**
     * 自动填充实体类的方法
     *
     * @param beanClass 实体类的class
     * @throws Exception
     */
    private static Map<String,Object> fillBeanData(Class beanClass,Map<String,String> genericMap) throws Exception {
        //Object obj = beanClass.newInstance()
        Map<String,Object> beanMap=new HashMap<>();
        fillBeanDataComm(beanClass, beanMap,genericMap);
        return beanMap;
    }

    /**
     * 填充数据重载---调用者提供对象实例
     *
     * @param beanClass
     * @param beanObj
     * @throws Exception
     */
    private static void fillBeanData(Class beanClass, Map<String,Object> beanObj,Map<String,String> genericMap) throws Exception {
        fillBeanDataComm(beanClass, beanObj,genericMap);
    }

    /**
     * 解析类中的字段并填充数据
     *
     * @param beanClass
     * @param beanObj
     * @throws Exception
     */
    private static void fillBeanDataComm(Class beanClass, Map<String,Object> beanObj,Map<String,String> genericMap) throws Exception {
        //Method[] methods=beanClass.getDeclaredMethods()
        ClassAndParentClassBO classAndParentClass = getParentField(beanClass);
        List<Method> allMethodList=classAndParentClass.getMethodList();
        List<String> allClassPath=classAndParentClass.getClassPathList();
        Map<String,String> fieldCommentMap=CommentReaderUtil.getClassFieldCommentByList(allClassPath);
        for (Method method : allMethodList) {
            String methodName = method.getName();
            if (methodName.startsWith(CommonConstant.SET_STR)) {
                Class fieldClass = method.getParameterTypes()[0];
                setFieldVal(beanClass, fieldClass, method, beanObj,genericMap,fieldCommentMap);
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
    private static void setFieldVal(Class beanClass, Class fieldClass, Method method, Map<String,Object> beanObj,Map<String,String> genericMap,Map<String,String> fieldCommentMap) throws Exception {
        Object fillData=oftenTypeFillData(fieldClass);
        String fieldName=methodNameToFieldName(method.getName());
        Field beanField=getSuperFieldByFieldName(beanClass,fieldName);
        String fieldKey=getFieldKeyByMethod(method,beanField,fieldCommentMap);
        if(fillData!=null){
            //method.invoke(beanObj,fillData)
            beanObj.put(fieldKey,fillData);
        }else if (fieldClass.equals(List.class)) {
            Class tempClass = beanClass;
            Field field=getSuperFieldByFieldName(tempClass,fieldName);
            if(field==null){
                return;
            }
            Type type = field.getGenericType();
            listOperation(beanClass,beanObj,method,type,genericMap,fieldCommentMap);
        }else if(fieldClass.getTypeName().equals(CommonConstant.TYPE_INT_ARR)){
            int[] tempInt={CommonConstant.INT_ONE,CommonConstant.INT_TWO};
            //method.invoke(beanObj,tempInt)
            beanObj.put(fieldKey,tempInt);
        }else {//自定义类
            Field tempField=beanClass.getDeclaredField(fieldName);
            String selfClassTypeName=tempField.getGenericType().getTypeName();
            if(selfClassTypeName.equals(CommonConstant.TYPE_T)){
                if(genericMap!=null){
                    //泛型的处理方式
                    String classType=beanClass.getTypeName();
                    String genericClass=genericMap.get(classType);
                    if(genericClass!=null){
                        Class genericClazz= getClassByPath(genericClass);
                        Object genericTypeVal=oftenTypeFillData(genericClazz);
                        if(genericTypeVal==null){
                            if(genericClazz.equals(List.class)){
                                Class tempClass = beanClass;
                                Field field=getSuperFieldByFieldName(tempClass,fieldName);
                                listOperation(genericClazz,beanObj,method,field.getGenericType(),genericMap,fieldCommentMap);
                            }else{
                                Map<String,Object> genericObjMap=new HashMap<>();
                                fillBeanDataComm(genericClazz,genericObjMap,genericMap);
                                beanObj.put(fieldKey,genericObjMap);
                            }
                        }else{
                            //常用类型
                            beanObj.put(fieldKey,genericTypeVal);
                        }
                    }
                }else{
                    //未写泛型类型
                    beanObj.put(fieldKey,new HashMap<>());
                }
            }else{
                //自定义类处理方式
                Map<String,Object> childBeanMap=new HashMap<>();
                Method[] childClassMethods = fieldClass.getDeclaredMethods();
                Map<String,String> selfClassCommentMap=CommentReaderUtil.getClassFieldComment(fieldClass.getName());
                Map<String,String> selfGenericMap=resolveGenericClass(selfClassTypeName);
                for (Method childClassMethod : childClassMethods) {
                    String childClassMethodName = childClassMethod.getName();
                    if (childClassMethodName.startsWith(CommonConstant.SET_STR)) {
                        Class childClassFieldClass = childClassMethod.getParameterTypes()[0];
                        setFieldVal(fieldClass, childClassFieldClass, childClassMethod, childBeanMap,selfGenericMap,selfClassCommentMap);
                        beanObj.put(fieldKey,childBeanMap);
                    }
                }
            }
        }
    }

    /**
     * 自定义类的泛型类型解析
     * @param classType
     * @return
     * @throws Exception
     */
    public static Map<String,String> resolveGenericClass(String classType) throws Exception{
        Map<String,String> genericMap=new HashMap<>();
        int leftIndex=0;
        int rightIndex;
        rightIndex=classType.indexOf(CommonConstant.LEFT_ANGLE_BRACKET);
        if(rightIndex>0){
            List<String> typeList=new ArrayList<>();
            while(rightIndex>0){
                String typeStr=classType.substring(leftIndex,rightIndex);
                leftIndex=rightIndex+1;
                rightIndex=classType.indexOf(CommonConstant.LEFT_ANGLE_BRACKET,leftIndex);
                typeList.add(typeStr);
                if(rightIndex==-1){
                    int lastRightIndex=classType.indexOf(CommonConstant.RIGHT_ANGLE_BRACKET);
                    String lastType=classType.substring(leftIndex,lastRightIndex);
                    typeList.add(lastType);
                }
            }
            Integer j;
            for(int i=CommonConstant.INT_ZERO;i<typeList.size();i++){
                String genericType=CommonConstant.NUL_STR;
                j=i+1;
                if(j< typeList.size()){
                    genericType=typeList.get(j);
                }
                genericMap.put(typeList.get(i),genericType);
            }
            genericMap.put(CommonConstant.KEY_RAP_UTIL_FIRST_CLASS,typeList.get(0));
        }
        return genericMap;
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

    /**
     * List类型处理
     * @param beanClass
     * @param beanObj
     * @param method
     * @param genericMap
     * @throws Exception
     */
    private static void listOperation(Class beanClass,Map<String,Object> beanObj,Method method,Type type,Map<String,String> genericMap,Map<String,String> fieldCommentMap) throws Exception{
        List<Object> objList = new ArrayList<>();
        if (type != null) {
            Class listObjClass;
            if(type.getTypeName().equals(CommonConstant.TYPE_T)){
                String genericTypePath=genericMap.get(beanClass.getTypeName());
                listObjClass=getClassByPath(genericTypePath);
            }else{
                ParameterizedType pt = (ParameterizedType) type;
                if(pt.getActualTypeArguments()[CommonConstant.INT_ZERO] instanceof TypeVariable){
                    String genericTypePath=genericMap.get(beanClass.getTypeName());
                    listObjClass=getClassByPath(genericTypePath);
                }else{
                    listObjClass = (Class) pt.getActualTypeArguments()[0];
                }
            }
            for (int i = CommonConstant.INT_ZERO; i < CommonConstant.ARR_FILL_MAX_NUM; i++) {
                Object listObj = fillListData(listObjClass);
                objList.add(listObj);
            }
            String fieldName=methodNameToFieldName(method.getName());
            String fieldKey;
            if(beanClass.equals(List.class)){
                fieldKey=fieldName+CommonConstant.KEY_COMMENT_SPLIT+fieldCommentMap.get(fieldName);
            }else{
                Field beanField=getSuperFieldByFieldName(beanClass,fieldName);
                fieldKey=getFieldKeyByMethod(method,beanField,fieldCommentMap);
            }
            //method.invoke(beanObj, objList)
            beanObj.put(fieldKey,objList);
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
                if(classTypeName.equals(CommonConstant.TYPE_OBJECT)){
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
            Map<String,Object> listFieldMap=new HashMap<>();
            //listParamObj = listParamClass.newInstance()
            fillBeanData(listParamClass, listFieldMap,null);
            listParamObj=listFieldMap;
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
            listParamObj=CommonConstant.FILL_STRING;
        } else if (classParam.equals(Integer.class) || classParam.equals(int.class)) {
            listParamObj = CommonConstant.FILL_INT;
        } else if (classParam.equals(Long.class) || classParam.equals(long.class)) {
            listParamObj = CommonConstant.FILL_LONG;
        } else if (classParam.equals(Float.class) || classParam.equals(float.class)) {
            listParamObj = CommonConstant.FILL_FLOAT;
        } else if (classParam.equals(Double.class) || classParam.equals(double.class)) {
            listParamObj = CommonConstant.FILL_DOUBLE;
        }else if(classParam.equals(Short.class)||classParam.equals(short.class)){
            listParamObj=CommonConstant.FILL_SHORT;
        } else if (classParam.equals(Date.class)) {
            listParamObj = LocalDateTime.now();
        }else if(classParam.equals(LocalDate.class)){
            listParamObj=LocalDate.now();
        }else if(classParam.equals(LocalTime.class)){
            listParamObj=LocalTime.now();
        } else if (classParam.equals(Boolean.class)||classParam.equals(boolean.class)) {
            listParamObj = Boolean.TRUE;
        } else if (classParam.equals(Byte.class)||classParam.equals(byte.class)) {
            listParamObj = CommonConstant.FILL_BYTE;
        }else if(classParam.equals(BigDecimal.class)){
            listParamObj=CommonConstant.FILL_BIGDECIMAL;
        }
        return listParamObj;
    }

    /**
     * 常用类型的数据填充
     * @param classPath
     * @return
     */
    private static Object oftenTypeFillData(String classPath) throws Exception{
        Class classParam=getClassByPath(classPath);
        return oftenTypeFillData(classParam);
    }

    /**
     * 根据方法获取字段key
     * @param method
     * @param fieldCommentMap
     * @return
     */
    private static String getFieldKeyByMethod(Method method,Field beanField,Map<String,String> fieldCommentMap){
        String fieldName=methodNameToFieldName(method.getName());
        String fieldComment=fieldCommentMap.get(fieldName);
        if(fieldComment==null){
            fieldComment="";
        }
        if(beanField==null){
            return fieldName+CommonConstant.KEY_COMMENT_SPLIT+fieldComment;
        }
        NotNull fieldType=beanField.getAnnotation(NotNull.class);
        if(fieldType!=null){
            String notnullMessage=fieldType.message();
            if(StringUtils.isNotBlank(notnullMessage)){
                Integer notnullIndex=notnullMessage.indexOf(CommonConstant.NOT_NULL_SPLIT_LONG_CHR);
                if(notnullIndex>-1){
                    fieldComment=notnullMessage.substring(0,notnullIndex);
                }else{
                    notnullIndex=notnullMessage.indexOf(CommonConstant.NOT_NULL_SPLIT_SHORT_CHR);
                    if(notnullIndex>-1){
                        fieldComment=notnullMessage.substring(0,notnullIndex);
                    }else{
                        fieldComment=notnullMessage;
                    }
                }
            }
            fieldComment=CommonConstant.FIELD_NOT_NULL+fieldComment;
        }
        String fieldKey= fieldName+CommonConstant.KEY_COMMENT_SPLIT +fieldComment;
        return fieldKey;
    }

    /**
     * 方法名转换为字段名
     *
     * @param methodName
     * @return
     */
    private static String methodNameToFieldName(String methodName) {
        String fieldName = methodName;
        if (methodName.startsWith(CommonConstant.SET_STR)&&fieldName.length()>CommonConstant.INT_THREE) {
            fieldName = methodName.substring(CommonConstant.INT_THREE, CommonConstant.INT_FOUR).toLowerCase() + methodName.substring(CommonConstant.INT_FOUR);
        }
        return fieldName;
    }

}
