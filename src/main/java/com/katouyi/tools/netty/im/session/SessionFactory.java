package com.katouyi.tools.netty.im.session;

public class SessionFactory {

    private final static Session session = new SessionMemoryImpl();

    public static Session getSession() {
        return session;
    }
}
