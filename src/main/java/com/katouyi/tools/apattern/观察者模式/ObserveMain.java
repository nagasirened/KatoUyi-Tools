package com.katouyi.tools.apattern.观察者模式;

import com.katouyi.tools.apattern.观察者模式.impl.DefaultSubject;
import com.katouyi.tools.apattern.观察者模式.impl.ObserveImpl;

public class ObserveMain {

    public static void main(String[] args) {
        // 创建一个主题
        DefaultSubject subject = new DefaultSubject("customer");

        // 创建两个观察者
        ObserveImpl num1 = new ObserveImpl( "天宫1号" );
        num1.bind( subject );
        ObserveImpl num2 = new ObserveImpl( "红旗2号" );
        num2.bind( subject );

        subject.notifyObserve( "起飞" );
    }

}
