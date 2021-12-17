package com.katouyi.tools.rpc.thrift.impl;

import com.alibaba.fastjson.JSON;
import com.katouyi.tools.rpc.thrift.Numberz;
import com.katouyi.tools.rpc.thrift.User;
import com.katouyi.tools.rpc.thrift.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserServiceImpl implements UserService.Iface {

    public final List<User> userList = new ArrayList<>();

    @Override
    public User getUserById(long id) {
        for (User user : userList) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    @Override
    public boolean addUser(User user) {
        for (User item : userList) {
            if (item.getId() == user.getId()) {
                return false;
            }
        }
        userList.add(user);
        System.out.println(JSON.toJSONString(userList));
        return true;
    }

    @Override
    public List<User> getAllUsers() {
        return userList;
    }

    @Override
    public Map<String, Numberz> getAllEnums() {
        return Stream.of(Numberz.values()).collect(Collectors.toMap(Numberz::name, item -> item));
    }
}
