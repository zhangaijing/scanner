package com.zyy.scanner.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StringUtils;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.RootDoc;

/**
 * @Author zhangyy
 * @DateTime 2019-01-26 17:38
 * @Description 注释解析工具类
 */
public class CommentReaderUtil {
    private static RootDoc root;
    public static class Doclet{
        public Doclet(){}
        public static boolean start(RootDoc root){
            CommentReaderUtil.root=root;
            return true;
        }
    }

    /**
     * 动态获取类的属性注释
     * @param classPath
     * @return
     */
    public static Map<String,String> getClassFieldComment(String classPath) throws Exception{
        Integer sdkIndex=classPath.indexOf(".sdk.");
        String projectAbsPath=getProjectPath();
        if(sdkIndex>0){
            Integer slantLastIndex=projectAbsPath.lastIndexOf("-");
            String tempProjectPath=projectAbsPath.substring(0,slantLastIndex+1);
            projectAbsPath=tempProjectPath+"sdk";
        }
        String targetWorkspace=projectAbsPath+"\\target";
        String classAbsPath=getClassAbsPathByClassPath(projectAbsPath,classPath);
        if(classAbsPath.lastIndexOf(".java")==-1){
            classAbsPath+=".java";
        }
        List<String> pathList=new ArrayList<>();
        pathList.add(classAbsPath);
        return getClassFieldComment(pathList,targetWorkspace);
    }

    /**
     * 获取项目名称
     * @return
     * @throws Exception
     */
    public static String getProjectName() throws Exception{
        String projectPath=getProjectPath();
        Integer slantLastIndex=projectPath.lastIndexOf("\\");
        String projectName="";
        if(slantLastIndex>0){
            projectName=projectPath.substring(slantLastIndex);
            if(projectName.startsWith("\\")){
                projectName=projectName.substring(1);
            }
        }
        return projectName;
    }

    /***
     * 类和字段注释获取
     * @return
     */
    private static Map<String,String> getFieldComment(){
        Map<String,String> fieldMap=new HashMap<>();
        ClassDoc[] classes=root.classes();
        for(int i=0;i<classes.length;++i){
            ClassDoc classDoc=classes[i];
            String classComment=classDoc.commentText();
            if(StringUtils.isEmpty(classComment)){
                classComment=classDoc.getRawCommentText();
            }
            fieldMap.put("classComment",classComment);
            FieldDoc[] fieldDocs=classDoc.fields(false);
            for(FieldDoc field:fieldDocs){
                fieldMap.put(field.name(),field.commentText());
            }
        }
        return fieldMap;
    }

    public static RootDoc getRoot(){
        return root;
    }

    public CommentReaderUtil(){}

    /**
     * 动态获取类的属性注释
     * @param classPathList
     * @return
     */
    private static Map<String,String> getClassFieldComment(List<String> classPathList,String targetWoraspace){
        Map<String,String> fieldMap=new HashMap<>();
        if(CollectionUtils.isNotEmpty(classPathList)){
            int classSize=classPathList.size();
            int paramListSize=6+classSize;
            String[] paramArr=new String[paramListSize];
            paramArr[0]="-doclet";
            paramArr[1]=Doclet.class.getName();
            paramArr[2]="-encoding";
            paramArr[3]="utf-8";
            paramArr[4]="-classpath";
            paramArr[5]=targetWoraspace;
            for(int i=0;i<classPathList.size();i++){
                paramArr[6+i]=classPathList.get(i);
            }
            com.sun.tools.javadoc.Main.execute(paramArr);
            fieldMap=getFieldComment();
        }
        return fieldMap;
    }

    /**
     * 类包路径转换为项目的类绝对路径
     * @param projectPath
     * @param classPath
     * @return
     * @throws Exception
     */
    private static String getClassAbsPathByClassPath(String projectPath,String classPath) throws Exception{
        String projectAbsPath=projectPath;
        projectAbsPath=projectAbsPath.replaceAll("\\\\","\\\\\\\\");
        projectAbsPath+="\\\\src\\\\main\\\\java";
        classPath=classPath.replaceAll("\\.","\\\\\\\\");
        String classAbsPath=projectAbsPath+"\\"+classPath;
        return classAbsPath;
    }

    /**
     * 类包路径转换为项目的类绝对路径
     * @param classPath
     * @return
     */
    private static String getClassAbsPathByClassPath(String classPath) throws Exception{
        String projectAbsPath=getProjectPath();
        projectAbsPath=projectAbsPath.replaceAll("\\\\","\\\\\\\\");
        projectAbsPath+="\\\\src\\\\main\\\\java";
        classPath=classPath.replaceAll("\\.","\\\\\\\\");
        String classAbsPath=projectAbsPath+"\\"+classPath;
        return classAbsPath;
    }

    /**
     * 获取项目路径
     * @return
     */
    private static String getProjectPath() throws Exception{
        File directory = new File("");
        String projectPath = directory.getCanonicalPath();
        return projectPath;
    }

}
