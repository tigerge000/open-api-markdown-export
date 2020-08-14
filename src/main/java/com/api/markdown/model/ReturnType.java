package com.api.markdown.model;

import lombok.Data;

/**
 * @author 飞狐 on 2019/04/15
 */
@Data
public class ReturnType extends Member {

    //
    private String name;
    /**
     * 参数描述
     */
    private String description;
}
