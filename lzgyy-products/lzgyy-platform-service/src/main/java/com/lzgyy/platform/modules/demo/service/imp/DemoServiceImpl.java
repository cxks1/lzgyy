package com.lzgyy.platform.modules.demo.service.imp;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.lzgyy.core.entity.PageQueryHelp;
import com.lzgyy.platform.modules.demo.dao.DemoJdbcDao;
import com.lzgyy.platform.modules.demo.service.DemoService;

@Service("demoService")
public class DemoServiceImpl implements DemoService {

	@Autowired
	private DemoJdbcDao demoJdbcDao;

	@Override
	public JSONObject get(Integer id, HttpServletRequest request) throws Exception {
		
		// 获取上下文（Context）信息
		/*System.out.println("获取上下文（Context）信息  Client address is " + request.getRemoteAddr());
		System.out.println("获取上下文（Context）信息 Client address is " + RpcContext.getContext().getRemoteAddressString());
		
		if (RpcContext.getContext().getRequest() != null && RpcContext.getContext().getRequest() instanceof HttpServletRequest) {
		    System.out.println("Client address is " + ((HttpServletRequest) RpcContext.getContext().getRequest()).getRemoteAddr());
		}

		if (RpcContext.getContext().getResponse() != null && RpcContext.getContext().getResponse() instanceof HttpServletResponse) {
		    System.out.println("Response object from RpcContext: " + RpcContext.getContext().getResponse());
		}*/
		
		return demoJdbcDao.get(id);
	}

	@Override
	public JSONObject getById(Integer id) throws Exception {

		return demoJdbcDao.get(id);
	}

	@Override
	public List<JSONObject> getByName(String name) throws Exception {
		JSONObject jsonParam = new JSONObject();
		jsonParam.put("name", name);
		return demoJdbcDao.get(jsonParam);
	}

	@Override
	public List<JSONObject> getList(JSONObject jsonParam) throws Exception {
		System.out.println("======传递到后台的数据  ====》 "+ new JSONObject().toJSONString(jsonParam));
		Integer[] intArr = PageQueryHelp.getArrStartLimitSize(jsonParam);
		List<JSONObject> list = demoJdbcDao.getList(jsonParam, intArr[0], intArr[1]);
		System.out.println("===========查询成功===== " + list.toString());
		return list;
	}

	@Override
	public int getTotal(JSONObject jsonParam) throws Exception {
		
		return demoJdbcDao.getTotal(jsonParam);
	}

	@Override
	public int insert(JSONObject jsonObject) throws Exception {
		
		return demoJdbcDao.insert(jsonObject);
	}

	@Override
	public int update(JSONObject json) throws Exception {
		
		return demoJdbcDao.update(json);
	}

	@Override
	public int delete(Integer id) throws Exception {
		JSONObject jsonParam = new JSONObject();
		jsonParam.put("id", id);
		jsonParam.put("delete_state", "0");
		return demoJdbcDao.update(jsonParam);
	}

	@Override
	public int deletePhysical(List<Integer> idsList) {
		return demoJdbcDao.deletePhysical(idsList);
	}
}