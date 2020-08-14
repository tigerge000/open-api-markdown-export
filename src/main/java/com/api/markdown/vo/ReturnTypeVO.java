package com.api.markdown.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author tanhuayou on 2019/05/05
 */
@Data
public class ReturnTypeVO implements Serializable {
    private String className;
    private String notes;
    private ModelVO model;
}
