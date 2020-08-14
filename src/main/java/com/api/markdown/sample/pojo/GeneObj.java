package com.api.markdown.sample.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 飞狐 on 2019/04/12
 */
@Data
@ApiModel(value = "ReturnType", description = "返回结构")
public class GeneObj<T> implements Serializable {
    @ApiModelProperty("数据")
    private T data;
    @ApiModelProperty("响应码")
    private int code;
}
