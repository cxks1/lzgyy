package com.lzgyy.platform.modules.test.service;

import java.util.List;

import com.lzgyy.core.entity.PageQuery;
import com.lzgyy.platform.modules.test.entity.Test;

public interface TestService {

	Test get(Long id) throws Exception;
	
	Test getById(Long id) throws Exception;
	
	List<Test> getByName(String name) throws Exception;
	
	List<Test> getList(PageQuery<Test> pageQuery) throws Exception;
	
	List<Test> getList2(Test test, Integer startPage, Integer limitSize) throws Exception;
	
	int getTotal(Test test) throws Exception;
	
	int insert(Test test) throws Exception;
	
	int update(Test test) throws Exception;
	
	int deleteIds(List<Long> idsList) throws Exception;
	
	int deletePhysical(List<Long> idsList) throws Exception;
	
	int updateIdsRestore(List<Long> idsList) throws Exception;
	
}