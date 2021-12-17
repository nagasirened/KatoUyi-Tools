package com.katouyi.tools.rpc.thrift.springAndPool.pool;

import lombok.Data;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.transport.TTransport;

@Data
public class ConnectionProviderImpl implements ConnectProvider {

    private String ip;

    private int port;

    private int readTimeout;

    private int maxIdel = 50;

    private int minIdel = 30;

    /**
     * 取对象时 是否测试可用
     */
    private boolean testOnBorrow = false;

    /**
     * 还对象时 是否测试可用
     */
    private boolean testOnReturn = false;

    /**
     * 空闲的时候 是否测试可用
     */
    private boolean testWhileIdel = true;

    private GenericObjectPool<TTransport> objectPool;

    public void init() {
        ThriftPoolableObjectFactory factory = new ThriftPoolableObjectFactory(ip, port, readTimeout);
        GenericObjectPoolConfig<TTransport> config = new GenericObjectPoolConfig<>();
        config.setMaxIdle(maxIdel);
        config.setMinIdle(minIdel);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setTestWhileIdle(testWhileIdel);
        // 链接耗尽时应该怎么做 不需要block,直接返回
        config.setBlockWhenExhausted(false);

        objectPool = new GenericObjectPool<>(factory, config);
    }

    public void destory() {
        try {
            objectPool.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TTransport getConnection() {
        try {
            return objectPool.borrowObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void returnConnection(TTransport tSocket) {
        try {
            objectPool.returnObject(tSocket);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}