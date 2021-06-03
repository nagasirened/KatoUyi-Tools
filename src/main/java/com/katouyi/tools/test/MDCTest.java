package com.katouyi.tools.test;

import org.slf4j.MDC;

public class MDCTest {

    public static void main(String[] args) {
        MDC.put("testKey", "testValue");
        testA();
    }

    public static String testA(){
        String val = MDC.get("testKey");
        System.out.println(val);
        return val;
    }
}
