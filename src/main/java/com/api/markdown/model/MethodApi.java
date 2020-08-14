package com.api.markdown.model;

import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author 飞狐 on 2019/04/13
 */
@Data
public class MethodApi implements Serializable {
    /**
     * 暴露的方法名字
     */
    private String name;
    /**
     * 参数列表
     */
    private List<Parameter> parameters;
    /**
     * 返回值类型
     */
    private ReturnType returnType;
    /**
     * 此方法涉及的数据模型
     */
    private Map<String, Model> models;

    /**
     * 方法名字
     *
     * @see NameAbbreviator#abbreviate(String)
     */
    private String apiName;

    /**
     * 方法说明
     *
     * @see ApiOperation#value()
     */
    private String desc;
}
