package com.lzgyy.platform.modules.test.mapper;

import java.util.List;
import java.util.Map;

import com.lzgyy.platform.modules.test.entity.Test;

public interface TestMapper {

	int insert(Test test);
	
	int update(Test test);

	List<Test> getList(Map<String,Object> paramMap);
	
	int getTotal(Map<String,Object> paramMap);
	
	int deleteIds(List<Long> idsList);
	
	int deleteIdsPhysical(List<Long> idsList);
	
	int updateIdsRestore(List<Long> idsList);
}
