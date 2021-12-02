package com.katouyi.tools.thriftServices.springAndPool.pool;

import org.apache.thrift.transport.TTransport;

/**
 * 通过ThreadLocal 实现连接的获取和返还
 */
public class ConnectionManager {

    private static final ThreadLocal<TTransport> transportThreadLocal = new ThreadLocal<>();

    private ConnectProvider connectProvider;

    public TTransport getConnection() {
        TTransport connection = connectProvider.getConnection();
        transportThreadLocal.set(connection);
        return connection;
    }

    public void returnConnection() {
        try {
            TTransport tTransport = transportThreadLocal.get();
            connectProvider.returnConnection(tTransport);
        }finally {
            transportThreadLocal.remove();
        }
    }
    public void setConnectionProvider(ConnectProvider connectionProvider) {
        this.connectProvider = connectionProvider;
    }
}