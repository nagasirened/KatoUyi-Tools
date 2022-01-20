package com.katouyi.tools.netty.im.group;

public abstract class GroupSessionFactory {

    private final static GroupSession session = new GroupSessionMemoryImpl();

    public static GroupSession getGroupSession() {
        return session;
    }
}