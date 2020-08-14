package com.api.markdown.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 飞狐 on 2019/04/15
 */
@Data
public class Member implements Serializable {
    /**
     * 如果是 Array时 则是其元素类型
     *
     * @see Model#getTypeName()
     */
    private String typeName;
    /**
     * map的v类型,此时 typeName代表map的k类型
     *
     * @see DataType#MAP
     */
    private String typeValue;
    /**
     * @see Class#getName()
     */
    private String className;
    /**
     * 数据类型
     */
    private DataType dataType;
}
