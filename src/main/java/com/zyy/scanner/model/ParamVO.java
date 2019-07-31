package com.zyy.scanner.model;

import java.io.Serializable;

import lombok.Data;

/**
 * @Author zhangyy
 * @DateTime 2019-06-05 09:54
 * @Description  类扫描实体类
 */
@Data
public class ParamVO implements Serializable {

    /*** 参数名 */
    private String paramName;

    /*** 参数类型 */
    private String paramType;

}
