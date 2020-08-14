## 根据swagger注解，自动生成对外接口文档


## 1、查询订单信息
#### API
> c.g.o.p.e.s.opendemo#queryorder
#### 参数列表
|参数名|参数类型|必填|说明|
|---|---|---|---|
|shopId|NUMBER|true|店铺id|
|orderId|NUMBER|true|订单id|
#### 返回值说明
[ReturnType](#3828be1f8426ada2c168a9cc8e27e4f9ec404fc0b1625d3dc924ba6f766cf846)
#### 用到的数据结构

> 返回结构
**<span id="3828be1f8426ada2c168a9cc8e27e4f9ec404fc0b1625d3dc924ba6f766cf846">ReturnType:</span>**
|字段名|字段类型|必填|说明|
|---|---|---|---|
|data|[Order](#3828be1f8426ada2c168a9cc8e27e4f97f5ec7475ddc563438bca9fafc20d472)|false|数据|
|code|NUMBER|false|响应码|


> 用户模型
**<span id="3828be1f8426ada2c168a9cc8e27e4f97f05326833e5023a53a7e48da286d0e4">user:</span>**

|字段名|字段类型|必填|说明|
|---|---|---|---|
|name|STRING|true|姓名|
|gender|STRING|false|性别,取值[MALE,LADY]|
|age|NUMBER|false|年龄,1~999|
|email|STRING|false|邮箱|
|underling|Map&lt;STRING,[user](#3828be1f8426ada2c168a9cc8e27e4f97f05326833e5023a53a7e48da286d0e4)&gt;|false|下属|


> 订单信息
**<span id="3828be1f8426ada2c168a9cc8e27e4f97f5ec7475ddc563438bca9fafc20d472">Order:</span>**

|字段名|字段类型|必填|说明|
|---|---|---|---|
|sender|[user](#3828be1f8426ada2c168a9cc8e27e4f97f05326833e5023a53a7e48da286d0e4)|false|发货人|
|receiver|[user](#3828be1f8426ada2c168a9cc8e27e4f97f05326833e5023a53a7e48da286d0e4)|false|收货人|
|price|BOOLEAN|false|价格|

