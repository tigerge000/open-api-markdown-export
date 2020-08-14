package com.api.markdown.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author tanhuayou on 2019/05/05
 */
@Data
public class ParameterVO implements Serializable {
    private String name;
    private String className;
    private String notes;
    private ModelVO model;
}
