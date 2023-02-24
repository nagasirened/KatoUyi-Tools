package com.katouyi.tools.apattern.观察者模式.inter;

/**
 * 观察者接口：所有观察者必须实现该接口
 */
public interface Observer {

    void invoke(String subjectName, String msg);

    void bind(Subject subject);
}
