package com.api.markdown.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * 对一个类的简单描述
 *
 * @author 飞狐 on 2019/04/13
 */
@Data
public class Model extends Member {
    /**
     * 字段
     */
    private List<ModelProperty> properties;
    /**
     * 模型名称
     *
     * @see ApiModel#value()
     */
    private String name;
    /**
     * 模型描述
     *
     * @see ApiModel#description()
     */
    private String description;
}
