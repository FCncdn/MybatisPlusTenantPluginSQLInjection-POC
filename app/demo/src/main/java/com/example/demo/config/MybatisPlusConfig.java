package com.example.demo.config;

//import com.baomidou.mybatisplus.core.parser.ISqlParser;
//import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
//import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
//import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.example.demo.common.TenantHolder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

@Configuration
public class MybatisPlusConfig {

    private static final String TENANT_ID = "tenant_id";


    // 3.4.x, 3.5.x
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        TenantLineInnerInterceptor tenantInterceptor = new TenantLineInnerInterceptor();
        tenantInterceptor.setTenantLineHandler(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                // 返回当前用户的租户ID
                return new StringValue(TenantHolder.getTenantId());
            }
        });
        interceptor.addInnerInterceptor(tenantInterceptor);
        return interceptor;

    }

    // 3.0.x, 3.1.x, 3.3.x
//    @Bean
//    public PaginationInterceptor paginationInterceptor() {
//        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
//
//        // SQL解析处理拦截：租户处理回调。
//        TenantSqlParser tenantSqlParser = new TenantSqlParser()
//                .setTenantHandler(new TenantHandler() {
//
//                    // 3.2.x, 3.3.x
//                    @Override
//                    public Expression getTenantId(boolean where) {
//                        // 从当前系统上下文中取出当前请求的租户ID，通过解析器注入到SQL中。
//                        String tenantId = TenantHolder.getTenantId();
//                        if (StringUtils.isEmpty(tenantId)) {
//                            throw new RuntimeException("tenantId is None.");
//                        }
//
//                        return new StringValue(tenantId);
//                    }
//
//
//                    // 3.0.x, 3.1.x
////                    @Override
////                    public Expression getTenantId() {
////                        // 从当前系统上下文中取出当前请求的租户ID，通过解析器注入到SQL中。
////                        String tenantId = TenantHolder.getTenantId();
////                        if (StringUtils.isEmpty(tenantId)) {
////                            throw new RuntimeException("tenantId is None.");
////                        }
////
////                        return new StringValue(tenantId);
////                    }
//
//                    @Override
//                    public String getTenantIdColumn() {
//                        return TENANT_ID;
//                    }
//
//                    @Override
//                    public boolean doTableFilter(String tableName) {
//                        return false;
//                    }
//                });
//        ArrayList<ISqlParser> sqlParserList = new ArrayList<ISqlParser>();
//        sqlParserList.add(tenantSqlParser);
//
//        paginationInterceptor.setSqlParserList(sqlParserList);
//        return paginationInterceptor;
//    }
}
