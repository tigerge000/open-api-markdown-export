package com.api.markdown.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author tanhuayou on 2019/05/05
 */
@Data
public class ModelVO implements Serializable {
    private String className;
    private Integer type;
    private Integer require;
    private String name;
    private String notes;
    private List<ModelVO> properties;
}
