package com.example.demo.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.common.TenantHolder;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class HelloController {

    @Autowired
    UserMapper userMapper;

    @GetMapping("/user")
    @ResponseBody
    public ArrayList<String> hello(@RequestParam(value = "tid", defaultValue = "DefaultTid") String tid){

        System.out.println("Tid: " + tid);
        TenantHolder.setTenantId(tid);

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lt("id", 10);
        List<User> userList = userMapper.selectList(wrapper);
        ArrayList<String> res = new ArrayList<>();
        userList.forEach(m -> res.add(m.toString()));
        return res;
    }
}

