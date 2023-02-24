package com.katouyi.tools.apattern.观察者模式.impl;


import com.katouyi.tools.apattern.观察者模式.inter.Observer;
import com.katouyi.tools.apattern.观察者模式.inter.Subject;

import java.util.LinkedList;
import java.util.List;

/**
 * 自定义实现的一个主题
 */
public class DefaultSubject implements Subject {

    List<Observer> observers = new LinkedList<>();

    private final String subjectName;

    public DefaultSubject(String subjectName) {
        this.subjectName = subjectName;
    }

    @Override
    public void register(Observer observer) {
        if ( observer == null ) return;
        observers.add( observer );
    }

    @Override
    public void unregister(Observer observer) {
        if ( observer == null ) return;
        observers.removeIf( exist -> exist.equals( observer ) );
    }

    @Override
    public void notifyObserve(String msg) {
        observers.forEach( item -> item.invoke( subjectName, msg ) );
    }



}
