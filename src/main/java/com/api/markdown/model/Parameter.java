package com.api.markdown.model;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * @author 飞狐 on 2019/04/13
 */
@Data
public class Parameter extends Member {
    /**
     * index
     */
    private int index;
    /**
     * 参数名
     * 注意：当使用 ApiImplicitParam(s)是按照其顺序解析的，
     * <p>
     * 推荐参数用一个对象来描述
     *
     * @see ApiParam#name()
     * @see ApiImplicitParam#name()
     * @see ApiImplicitParams
     */
    private String name;
    /**
     * 参数描述
     *
     * @see ApiParam#value()
     * @see ApiImplicitParam#value()
     */
    private String description;

    /**
     * 是否必须
     *
     */
    private boolean required;
}
