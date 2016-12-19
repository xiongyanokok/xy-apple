package com.xy.rpc;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Server {

	public static void main(String[] args) {
        new ClassPathXmlApplicationContext("rpc_server.xml");
        System.out.println("servier start success");
    }
}
