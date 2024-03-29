package com.katouyi.tools.rpc.proto.test1.protoStuff.test;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Data
public class User implements Serializable {

    private String username;

    private String password;

    private Integer age;

    private List<String> profession;

    private String address;

    // 嵌套
    private ExtraInfo extraInfo = new ExtraInfo("byteMan", 1);

    public static User testOne() {
        User user = new User();
        user.setUsername("ZengGuoFan");
        user.setPassword("123123");
        user.setAge(56);
        user.setProfession(Arrays.asList("official", "terrorist", "husband"));
        user.setAddress("湖南");
        return user;
    }

}
