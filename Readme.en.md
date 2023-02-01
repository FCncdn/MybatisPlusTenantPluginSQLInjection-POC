
# MybatisPlusTenantPluginSQLInjection

## 1. Vulnerability Summary

Threat: SQL Injection

MavenGroupId: com.baomidou

MavenArtifactId: mybatis-plus

AffectedVersion: 3.x

AffectedComponent: TenantPlugin

Description: The tenant plugin fails to handle the tenant ID value when constructing SQL expressions and directly concatenates it into the SQL expression. When the application has enabled the tenant plugin and the tenant ID is controllable by an external user, this can result in SQL injection.

Result：A successful SQL injection vulnerability can allow for sensitive data to be read from the database, modification of database data (insert/update/delete), execution of administrative operations on the database (such as shutting down the DBMS), recovery of content from files present in the DBMS file system and in certain cases, issuing commands to the operating system.

Prerequisites:
1. The application has enabled the tenant plugin;
2. The tenant ID is externally controllable and has been passed into the `getTenantId` method;
    - (3.0.x, 3.1.x, 3.3.x) `com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler#getTenantId`
    - (3.4.x, 3.5.x) `com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler#getTenantId`
3. The application has not filtered the tenant ID value.


## 2. Vulnerability Reproduction
Test Version: 3.4.2

Refer to "https://github.com/baomidou/mybatis-plus-samples/tree/master/mybatis-plus-sample-tenant" to build a spring application and enable the tenant plugin:

`com.example.demo.config.MybatisPlusConfig`
![Pasted image 20230201155305.png](./images/Pasted%20image%2020230201155305.png)

`com.example.demo.common.TenantHolder`
![Pasted image 20230201155404.png](./images/Pasted%20image%2020230201155404.png)

Interface for testing: `/user?tid=` will returns a specified tenant data record.

`com.example.demo.controller.HelloController`
![Pasted image 20230201155458.png](./images/Pasted%20image%2020230201155458.png)

db:
```text
CREATE TABLE `users` (  
  `id` int(32) NOT NULL AUTO_INCREMENT,  
  `name` varchar(32) NOT NULL,  
  `tenant_id` varchar(64) NOT NULL,  
  PRIMARY KEY (`id`)  
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
```

Test interface using blank parameters:

`http://localhost:8080/user?tid=`
![Pasted image 20230201155910.png](./images/Pasted%20image%2020230201155910.png)

Use `' or 1=1 and '123'='123` to perform sql injection and get all the data
![Pasted image 20230201155957.png](./images/Pasted%20image%2020230201155957.png)


## 3. Vulnerability Detail

Test Version: 3.0.7.1

builderExpression:265, TenantSqlParser (com.baomidou.mybatisplus.extension.plugins.tenant)
processPlainSelect:193, TenantSqlParser (com.baomidou.mybatisplus.extension.plugins.tenant)
processPlainSelect:174, TenantSqlParser (com.baomidou.mybatisplus.extension.plugins.tenant)
processSelectBody:75, TenantSqlParser (com.baomidou.mybatisplus.extension.plugins.tenant)
processParser:96, AbstractJsqlParser (com.baomidou.mybatisplus.core.parser)
parser:71, AbstractJsqlParser (com.baomidou.mybatisplus.core.parser)
sqlParser:63, AbstractSqlParserHandler (com.baomidou.mybatisplus.extension.handlers)
intercept:129, PaginationInterceptor (com.baomidou.mybatisplus.extension.plugins)

![Pasted image 20230201150219.png](./images/Pasted%20image%2020230201150219.png)

Test Version: 3.4.2

builderExpression:357, TenantLineInnerInterceptor (com.baomidou.mybatisplus.extension.plugins.inner)
processPlainSelect:235, TenantLineInnerInterceptor (com.baomidou.mybatisplus.extension.plugins.inner)
processSelectBody:100, TenantLineInnerInterceptor (com.baomidou.mybatisplus.extension.plugins.inner)
processSelect:88, TenantLineInnerInterceptor (com.baomidou.mybatisplus.extension.plugins.inner)
processParser:91, JsqlParserSupport (com.baomidou.mybatisplus.extension.parser)
parserSingle:50, JsqlParserSupport (com.baomidou.mybatisplus.extension.parser)
beforeQuery:70, TenantLineInnerInterceptor (com.baomidou.mybatisplus.extension.plugins.inner)
intercept:78, MybatisPlusInterceptor (com.baomidou.mybatisplus.extension.plugins)

![Pasted image 20230201154503.png](./images/Pasted%20image%2020230201154503.png)
