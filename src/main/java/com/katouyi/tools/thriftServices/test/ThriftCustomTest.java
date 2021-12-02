package com.katouyi.tools.thriftServices.test;


import com.katouyi.tools.thriftServices.User;
import com.katouyi.tools.thriftServices.UserService;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class ThriftCustomTest {

    public static void main(String[] args) throws Exception {
        TTransport transport = new TSocket("localhost", 8888);

        // 创建client
        TBinaryProtocol protocol = new TBinaryProtocol(transport);
        UserService.Client client = new UserService.Client(protocol);
        // 建立连接
        transport.open();

        // 添加用户
        User user = new User().setId(3).setUsername("Zeng").setAge((short) 28);
        System.out.println(client.addUser(user));;

        Thread.sleep(100000);

        transport.close();
    }
}
