package com.katouyi.tools.thriftServices.impl;

import com.alibaba.fastjson.JSON;
import com.katouyi.tools.thriftServices.Numberz;
import com.katouyi.tools.thriftServices.User;
import com.katouyi.tools.thriftServices.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;

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
