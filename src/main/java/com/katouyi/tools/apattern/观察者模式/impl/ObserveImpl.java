package com.katouyi.tools.apattern.观察者模式.impl;


import com.katouyi.tools.apattern.观察者模式.inter.Observer;
import com.katouyi.tools.apattern.观察者模式.inter.Subject;

/**
 * 观察者1
 */
public class ObserveImpl implements Observer {

    private final String name;

    public ObserveImpl(String name) {
        this.name = name;
    }

    public static ObserveImpl create(String name) {
        return new ObserveImpl( name );
    }

    /**
     * 主题推送消息
     */
    @Override
    public void invoke(String subjectName, String msg) {
        System.out.println( name + " 接收到了新的消息 " + msg);
    }

    @Override
    public void bind(Subject subject) {
        if ( subject == null ) return;
        subject.register( this );
    }

}
