package com.api.markdown.sample;


import com.api.markdown.sample.pojo.OrderInfo;
import com.ggj.platform.gsf.result.PlainResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


/**
 * @author 飞狐 on 2019/04/15
 * {@link Api#value()} 分组信息,比如订单、商品、交易...
 * {@link ApiOperation#value()} 此接口功能,比如 订单查询、上架商品、增减库存...
 * {@link ApiParam#name()} 字段名
 * {@link ApiParam#value()} 此字段描述
 * 也支持 {@link ApiImplicitParams}注解，使用此注解时需保证字段之间的顺序，推荐直接使用 {@link ApiParam}
 */
@Api(value = "订单")
public interface OpenDemoService {

    @ApiOperation("查询订单信息")
    OrderInfo queryOrder(@ApiParam(name = "shopId", value = "店铺id")
                                              Long shopId,
                                      @ApiParam(name = "orderId", value = "订单id")
                                              Long orderId);

    @ApiOperation("查询订单信息1")
    PlainResult<OrderInfo> queryOrder1(@ApiParam(name = "shopId", value = "店铺id")
                                               Long shopId,
                                       @ApiParam(name = "orderId", value = "订单id")
                                               Long orderId);
}
