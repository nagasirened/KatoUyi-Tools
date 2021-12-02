package com.katouyi.tools.thriftServices.springAndPool.pool;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 * 池对象 PooledObject
 * https://blog.csdn.net/qq447995687/article/details/80413227
 */

public class ThriftPoolableObjectFactory implements PooledObjectFactory<TTransport> {

    private final String ip;
    private final int port;
    private final int readTimeOut;

    public ThriftPoolableObjectFactory(String ip, int port, int readTimeOut) {
        this.ip = ip;
        this.port = port;
        this.readTimeOut = readTimeOut;
    }

    @Override
    public PooledObject<TTransport> makeObject() throws Exception {
        TTransport tTransport = (TTransport) new TSocket(null, ip, port, readTimeOut);
        tTransport.open();
        return new DefaultPooledObject<>(tTransport);
    }

    @Override
    public void destroyObject(PooledObject<TTransport> pooledObject) throws Exception {
        if (pooledObject.getObject().isOpen()) {
            pooledObject.getObject().close();
        }
        pooledObject.invalidate();
    }

    @Override
    public boolean validateObject(PooledObject<TTransport> pooledObject) {
        try {
            return pooledObject.getObject().isOpen();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 激活对象
     */
    @Override
    public void activateObject(PooledObject<TTransport> pooledObject) throws Exception {

    }

    /**
     * 钝化对象
     */
    @Override
    public void passivateObject(PooledObject<TTransport> pooledObject) throws Exception {

    }

}