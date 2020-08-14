package com.api.markdown.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 飞狐 on 2019/04/13
 */
@Data
public class ModelProperty extends Member {
    /**
     * 字段名
     */
    private String name;
    /**
     * 字段描述
     *
     * @see ApiModelProperty#value()
     * @see ApiModelProperty#notes()
     */
    private String description;
    /**
     * 是否必须
     *
     * @see ApiModelProperty#required()
     */
    private boolean required;

    /**
     * 是否隐藏
     */
    private boolean hidden;
}
