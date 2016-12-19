package com.xy.rpc;

import java.util.Map;

public class UserServiceImpl implements UserService {

	public String hello(String s) {
		return "xxxxxxxxxx--"+s;
	}
	
	public Map<String, Object> getObject(Map<String, Object> map) {
		map.put("last", "最后一个");
		return map;
	}
}
