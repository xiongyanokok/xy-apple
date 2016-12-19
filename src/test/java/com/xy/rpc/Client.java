package com.xy.rpc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.xy.apple.netty.RpcProxy;

public class Client {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("rpc_client.xml");
        //RpcProxy rpcProxy = context.getBean(RpcProxy.class);

        UserService userService = context.getBean(UserService.class);
        String result = userService.hello("熊焱");
        System.out.println(result);
        
       /* Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", "xiongyan");
        map.put("name", "熊焱");
        map.put("phone", "15001187708");
        
        map = userService.getObject(map);
        System.out.println(map);*/

        System.exit(0);
    }
}
