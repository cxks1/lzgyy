package com.lzgyy.platform.modules.test.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.lzgyy.common.lang.ObjectToMapUtils;
import com.lzgyy.core.constant.Const;
import com.lzgyy.core.entity.PageQuery;
import com.lzgyy.core.entity.PageQueryHelp;
import com.lzgyy.platform.modules.test.entity.Test;
import com.lzgyy.platform.modules.test.mapper.TestMapper;
import com.lzgyy.platform.modules.test.service.TestService;

@Service("testService")
public class TestServiceImpl implements TestService {

	@Resource
	private TestMapper testMapper;

	@Override
	public Test get(Long id) throws Exception {
		Test test = null;
		if(null != id){
			Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("id", id);
			List<Test> testList = testMapper.getList(paramMap);
			if(testList != null && testList.size() == 1){
				test = testList.get(0);
			}
		}
		return test;
	}

	@Override
	public Test getById(Long id) throws Exception {
		return get(id);
	}

	@Override
	public List<Test> getByName(String name) throws Exception {
		List<Test> testList = null;
		if(StringUtils.isNotBlank(name)){
			Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("name", name);
			testList = testMapper.getList(paramMap);
		}
		return testList;
	}

	@Override
	public List<Test> getList(PageQuery<Test> pageQuery) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("***********进入service**************");
		System.out.println("传递过来的值 " + new JSONObject().toJSONString(pageQuery));
		
		System.out.println("-------------------------");
		//System.out.println(new JSONObject().toJSONString(paramMap));
		List<Test> testList = testMapper.getList(PageQueryHelp.getParamMap(pageQuery));
		//System.out.println(testDao.getTotal(paramMap));
		return testList;
	}
	
	@Override
	public List<Test> getList2(Test test, Integer startPage, Integer limitSize) throws Exception{
		System.out.println("***********进入service**************");
		System.out.println("传递过来的值 " + new JSONObject().toJSONString(test));
		System.out.println("startPage的值 " + startPage);
		System.out.println("limitPage的值 " + limitSize);
		
		Map<String,Object> paramMap = ObjectToMapUtils.objectToIgnorNullMergeMap(test);
		paramMap.put(Const.PAGE_STARTSIZE, PageQueryHelp.getStartSize(startPage, limitSize));
		paramMap.put(Const.PAGE_LIMITSIZE, limitSize);
		List<Test> testList = testMapper.getList(paramMap);
		return testList;
	}

	@Override
	public int getTotal(Test test) throws Exception {
		return testMapper.getTotal(ObjectToMapUtils.objectToIgnorNullMergeMap(test));
	}

	@Override
	public int insert(Test test) throws Exception {
		return testMapper.insert(test);
	}

	@Override
	public int update(Test test) throws Exception {
		System.out.println("卧槽，走更新了。。。。。。。。。。。");
		System.out.println(new JSONObject().toJSONString(test));
		return testMapper.update(test);
	}

	@Override
	public int deleteIds(List<Long> idsList) throws Exception {
		return testMapper.deleteIds(idsList);
	}

	@Override
	public int deletePhysical(List<Long> idsList) throws Exception {
		return testMapper.deleteIdsPhysical(idsList);
	}

	@Override
	public int updateIdsRestore(List<Long> idsList) throws Exception {
		return testMapper.updateIdsRestore(idsList);
	}
}