package com.katouyi.tools.exercise.proxy.jdkProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyService {
    public static void main(String[] args) {
        UserService proxyInstance = (UserService) new ProxyService().getProxy(new UserServiceImpl());
        proxyInstance.call();
    }

    public Object getProxy(Object target) {
        ClassLoader classLoader = target.getClass().getClassLoader();
        Class<?>[] interfaces = target.getClass().getInterfaces();
        return Proxy.newProxyInstance(classLoader, interfaces, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("method pre");
                Object result = method.invoke(target, args);
                System.out.println("method post");
                return result;
            }
        });
    }
}
