package com.example.demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.common.TenantHolder;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    UserMapper userMapper;

    @Test
    public void testTenant(){
        String test_illegal_id = "whatever";
        String test_legal_id = "19065dc6-a14a-11ed-ba3f-864b77a22e79";

        QueryWrapper<User> wrapper = new QueryWrapper<>();

        TenantHolder.setTenantId(test_illegal_id);
        try{
            wrapper.lt("id", 10);
            List<User> userList = userMapper.selectList(wrapper);
            System.out.println("Id: " + TenantHolder.getTenantId());
            userList.forEach(e -> System.out.println(e.toString()));
            assertEquals(0, userList.size());

        }catch (Exception e){
            System.out.println(e.toString());
            System.out.println("Illegal tenant id: " + TenantHolder.getTenantId());
            assert true;
        }

        TenantHolder.setTenantId(test_legal_id);
        try{
            wrapper.lt("id", 10);
            List<User> userList = userMapper.selectList(wrapper);
            System.out.println("Id: " + TenantHolder.getTenantId());
            userList.forEach(e -> System.out.println(e.toString()));
            assertEquals(1, userList.size());
        }catch (Exception e){
            assert false;
            System.out.println(e.toString());
        }
    }

}
