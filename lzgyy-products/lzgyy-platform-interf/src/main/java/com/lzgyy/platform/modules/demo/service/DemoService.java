package com.lzgyy.platform.modules.demo.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import com.alibaba.fastjson.JSONObject;

public interface DemoService {
	
	JSONObject get(Integer id, HttpServletRequest request) throws Exception;
	
	JSONObject getById(Integer id) throws Exception;
	
	List<JSONObject> getByName(String name) throws Exception;
	
	List<JSONObject> getList(JSONObject jsonParam) throws Exception;
	
	int getTotal(JSONObject jsonParam) throws Exception;
	
	int insert(JSONObject jsonObject) throws Exception;
	
	int update(JSONObject json) throws Exception;
	
	int delete(Integer id) throws Exception;
	
	int deletePhysical(List<Integer> idsList) throws Exception;
}