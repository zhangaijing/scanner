package com.zyy.scanner.constant;

import java.math.BigDecimal;

/**
 * @Author zhangyy
 * @DateTime 2019-07-25 10:57
 * @Description
 */
public class CommonConstant {

    public final static String SCANNER_BASE_PAGE="com.hoolink";

    public final static Integer INT_ZERO=0;
    public final static Integer INT_FOUR=4;
    public final static Integer INT_THREE=3;
    public final static Integer INT_TWO=2;
    public final static Integer INT_ONE=1;
    public final static Integer ARR_FILL_MAX_NUM=INT_TWO;
    /*** key和注释的分隔符 */
    public final static String KEY_COMMENT_SPLIT="###";

    /*** 常用类型数据默认填充值 */
    public final static Integer FILL_INT=10;
    public final static String FILL_STRING="string";
    public final static Long FILL_LONG=99L;
    public final static Float FILL_FLOAT=22.22F;
    public final static Double FILL_DOUBLE=66.66D;
    public final static Short FILL_SHORT=66;
    public final static Byte FILL_BYTE=new Byte("1");
    public final static BigDecimal FILL_BIGDECIMAL=new BigDecimal(88.88).setScale(2,BigDecimal.ROUND_DOWN);

    /*** 常用类型 */
    public final static String TYPE_OBJECT="java.lang.Object";
    public final static String TYPE_T="T";
    public final static String TYPE_INT_ARR="int[]";
    public final static String TYPE_VOID="void";

    public final static String KEY_RAP_UTIL_FIRST_CLASS="rapUtilFirstClass";
    public final static String LEFT_ANGLE_BRACKET="<";
    public final static String RIGHT_ANGLE_BRACKET=">";
    public final static String NUL_STR="";
    public static final String SET_STR = "set";

    /*** 包扫描 */
    public final static String AUTHOR="@author";
    public final static String DESC_FULL="@description";
    public final static String DESC_SHORT="@desc";
    public final static String KEY_CLASS_COMMENT="classComment";
    public final static String ENTER_CHAR="\n";
    public final static String CONTROLLER_CHAR="controller";
    public final static String FILE_CHR="file";

    public final static String FIELD_NOT_NULL="[*]";
    public final static String NOT_NULL_SPLIT_LONG_CHR="不能为空";
    public final static String NOT_NULL_SPLIT_SHORT_CHR="不为空";

    /*** 类注释解析 */
    public final static String DOT_SDK_DOT_CHR=".sdk.";
    public final static String SDK_CHR="sdk";
    public final static String TARGET_CHR="\\target";
    public final static String JAVA_SUFFIX_CHR=".java";
    public final static String CLASS_COMMENT_CHR="classComment";
    public final static String DOCLET_CHR="-doclet";
    public final static String ENCODING_CHR="-encoding";
    public final static String UTF_CHR="utf-8";
    public final static String CLASS_PATH_CHR="-classpath";
    public final static String SRC_MAIN_JAVA_CHR="\\\\src\\\\main\\\\java";
}
