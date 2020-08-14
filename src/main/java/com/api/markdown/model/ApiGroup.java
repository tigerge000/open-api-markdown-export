package com.api.markdown.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 飞狐 on 2019/04/13
 */
@Data
public class ApiGroup implements Serializable {
    public static final String DEFAULT_GROUP = "_$DEFAULT$";

    /**
     * 导出的服务接口名称
     * 如： xxx.swagger.Exporter$InnerInterface
     *
     * @see Class#getComponentType()
     */
    private String type;
    /**
     * 所属分组
     * 取tags[0];
     * 若tags没有填写 则使用 value;
     * 若value也没填写则分配在默认分组;
     *
     * @see io.swagger.annotations.Api#tags()
     * @see io.swagger.annotations.Api#value()
     */
    private String group;
    private List<MethodApi> apis;
}
