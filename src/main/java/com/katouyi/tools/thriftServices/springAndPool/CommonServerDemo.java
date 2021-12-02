package com.katouyi.tools.thriftServices.springAndPool;


import com.katouyi.tools.thriftServices.UserService;
import com.katouyi.tools.thriftServices.impl.UserServiceImpl;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.layered.TFramedTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * 构建服务，并启动服务端 main/**
 * https://blog.csdn.net/supper10090/article/details/78119759
 */
public class CommonServerDemo {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final int port;
    private final Map<String, Object> exposedServerMap;

    public CommonServerDemo(int port, Map<String, Object> exposedServerMap) {
        this.port = port;
        this.exposedServerMap = exposedServerMap;
    }

    public static void main(String[] args) throws ClassNotFoundException {
        HashMap<String, Object> exposedServerMap = new HashMap<>();
        exposedServerMap.put(UserService.class.getName(), new UserServiceImpl());
        new CommonServerDemo(8080, exposedServerMap).start();
    }

    public void start() {
        try {
            TNonblockingServerSocket serverSocket = new TNonblockingServerSocket(port);
            TMultiplexedProcessor multiplexedProcessor = new TMultiplexedProcessor();
            for (Map.Entry<String, Object> entry : exposedServerMap.entrySet()) {
                String serviceInstanceName = entry.getKey();
                // 利用反射注册多个服务
                Class<?> processClass = Class.forName(serviceInstanceName + "$Processor");
                Class<?> iFaceClass = Class.forName(serviceInstanceName + "$Iface");
                Constructor<?> constructor = processClass.getConstructor(iFaceClass);
                Object impl = entry.getValue();
                TProcessor tProcessor = (TProcessor) constructor.newInstance(impl);

                multiplexedProcessor.registerProcessor(serviceInstanceName, tProcessor);
            }

            // 添加Args属性
            TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(serverSocket);
            args.processor(multiplexedProcessor);
            args.transportFactory(new TFramedTransport.Factory());
            args.protocolFactory(new TCompactProtocol.Factory());

            TServer server = new TThreadedSelectorServer(args);
            server.serve();
        } catch (Exception e) {
            logger.error("错误的开始", e);
        }
    }

}
