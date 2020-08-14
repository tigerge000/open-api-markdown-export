package com.api.markdown.sample.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 飞狐 on 2019/04/23
 */
@Data
@ApiModel(description = "订单信息", value = "OrderInfo")
public class OrderInfo implements Serializable {
    @ApiModelProperty(required = true, value = "发货人", hidden = true)
    private User sender;
    @ApiModelProperty("收货人")
    private User receiver;
    @ApiModelProperty("价格")
    private boolean price;
    @ApiModelProperty("二级订单")
    private OrderInfo children;
}
