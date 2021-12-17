package com.katouyi.tools.rpc.thrift.springAndPool.pool;

import com.katouyi.tools.rpc.thrift.User;
import com.katouyi.tools.rpc.thrift.UserService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.layered.TFramedTransport;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommonClientDemo<T> implements FactoryBean<T> {

    /**
     * 获取连接和返还连接
     */
    private ConnectionManager connectionManager;

    /**
     * 服务的Iface接口
     */
    private Class interfaces;

    /**
     * 服务的Client类构造器
     */
    private Constructor<?> constructor;

    /**
     * 服务类完整路径
     */
    private String serviceName;

    public CommonClientDemo(String hostName, int port, int timeOut, Class serviceClazz) {
        try {
            ConnectionProviderImpl connectionProvider = new ConnectionProviderImpl();
            connectionProvider.setIp(hostName);
            connectionProvider.setPort(port);
            connectionProvider.setReadTimeout(timeOut);
            connectionProvider.init();

            connectionManager = new ConnectionManager();
            connectionManager.setConnectionProvider(connectionProvider);
            serviceName = serviceClazz.getName();
            //获得service里面的Iface 接口
            interfaces = Class.forName(serviceName + "$Iface");
            //获得service里面的Client类
            Class<?> clientClass = Class.forName(serviceName + "$Client");
            constructor = clientClass.getConstructor(TProtocol.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getObject() {
        try {
            //创建代理
            ClassLoader classLoader = this.getClass().getClassLoader();
            return (T) Proxy.newProxyInstance(classLoader, new Class[]{interfaces}, new InvocationHandler() {
                @Override
                public Object invoke(Object obj, Method method, Object[] params) throws Throwable {
                    try {
                        TTransport tTransport = connectionManager.getConnection();
                        TFramedTransport tFramedTransport = new TFramedTransport(tTransport);
                        TProtocol protocol = new TCompactProtocol(tFramedTransport);
                        TMultiplexedProtocol map = new TMultiplexedProtocol(protocol, serviceName);

                        //创建client实例
                        Object target = constructor.newInstance(map);
                        return method.invoke(target, params);
                    } finally {
                        connectionManager.returnConnection();
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<?> getObjectType() {
        return interfaces;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public static void main(String[] args) throws TException {
        CommonClientDemo<UserService.Iface> commonClientDemo = new CommonClientDemo<>("localhost", 8089, 1000, UserService.class);
        UserService.Iface client = commonClientDemo.getObject();
        System.out.println(commonClientDemo.getObjectType());
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 10000; i++) {
            final int index = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        User user = client.getUserById(index);
                        System.out.println(user);
                    } catch (TException e) {
                        e.printStackTrace();
                    }

                }
            });

        }
        executorService.shutdown();
    }

}