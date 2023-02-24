package com.katouyi.tools.apattern.观察者模式.inter;


/**
 * 主题接口：所有主题必须实现该接口
 */
public interface Subject {

    /**
     * 注册一个观察者
     */
    void register(Observer observer);

    /**
     * 观察者取关
     */
    void unregister(Observer observer);

    /**
     * 通知所有的观察者
     */
    void notifyObserve(String msg);

}