package com.api.markdown.sample.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 飞狐 on 2019/04/12
 * {@link ApiModel#value()} 将作为对外展示的名称，若没有填写则使用 this.class.simpleName
 * {@link ApiModel#description()} 对这个模型的简单描述
 * {@link ApiModelProperty#value()} 对此字段的一个描述，譬如某些枚举值的定义等
 */
@Data
@ApiModel(description = "用户模型")
public class User implements Serializable {
    @ApiModelProperty(value = "姓名", required = true, hidden = true)
    private String name;
    @ApiModelProperty(value = "性别,取值[MALE,LADY]")
    private String gender;
    @ApiModelProperty(value = "年龄,1~999")
    private Integer age;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "下属")
    private Map<String, User> underling;
}
