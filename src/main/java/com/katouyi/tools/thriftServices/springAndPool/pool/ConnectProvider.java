package com.katouyi.tools.thriftServices.springAndPool.pool;

import org.apache.thrift.transport.TTransport;

public interface ConnectProvider {

    TTransport getConnection();

    void returnConnection(TTransport tSocket);
}
