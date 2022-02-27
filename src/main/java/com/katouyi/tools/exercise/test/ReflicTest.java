package com.katouyi.tools.exercise.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflicTest {

    public static void main(String[] args) throws Exception {
        Class<?> clazz = Class.forName(TargetObject.class.getCanonicalName());
        TargetObject target = (TargetObject) clazz.newInstance();

        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            System.out.println(method.getName());
        }

        Method publicMethod = clazz.getDeclaredMethod("publicMethod", String.class);
        publicMethod.invoke(target, "ZengGuangfu");

        Field field = clazz.getDeclaredField("value");
        field.setAccessible(true);
        field.set(target, "HuangYu");

        Method privateMethod = clazz.getDeclaredMethod("privateMethod");
        //为了调用private方法我们取消安全检查
        privateMethod.setAccessible(true);
        privateMethod.invoke(target);
    }
}

class TargetObject {
    private String value;

    public TargetObject() {
        value = "Zeng";
    }

    public void publicMethod(String s) {
        System.out.println("I love " + s);
    }

    private void privateMethod() {
        System.out.println("value is " + value);
    }
}
