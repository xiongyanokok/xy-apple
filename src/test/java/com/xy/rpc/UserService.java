package com.xy.rpc;

import java.util.Map;

public interface UserService {

	String hello(String s);
	
	Map<String, Object> getObject(Map<String, Object> map);
	
}
