package com.katouyi.tools.thriftServices.test;

import com.katouyi.tools.thriftServices.UserService;
import com.katouyi.tools.thriftServices.impl.UserServiceImpl;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import java.net.ServerSocket;

public class ThriftServerTest {

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8888);
        TServerSocket tServerSocket = new TServerSocket(serverSocket);
        TServer.Args tServerArgs = new TServer.Args(tServerSocket);
        UserService.Processor<UserServiceImpl> userServiceProcessor = new UserService.Processor<>(new UserServiceImpl());

        // TMulti可以注册多个processor
        TMultiplexedProcessor processors = new TMultiplexedProcessor();
        processors.registerProcessor("UserService", userServiceProcessor);

        // tServerArgs.processor(userServiceProcessor);

        tServerArgs.processor(processors);
        TSimpleServer server = new TSimpleServer(tServerArgs);
        System.out.println("启动thrift服务端");
        server.serve();
    }
}
