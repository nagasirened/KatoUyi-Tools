package com.katouyi.tools.exercise.proxy.jdkProxy;

public interface UserService {

    void call();
}


class UserServiceImpl implements UserService{
    @Override
    public void call() {
        System.out.println("my name is zengGu..");
    }
}
