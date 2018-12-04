package com.lzgyy.plugins.quartz.mapper;

import java.util.List;
import java.util.Map;

import com.lzgyy.plugins.quartz.entity.JobEntity;

public interface JobMapper{

	int insert(JobEntity test);
	
	int update(JobEntity test);

	List<JobEntity> getList(Map<String,Object> paramMap);
	
	int getTotal(Map<String,Object> paramMap);
	
	int deleteIds(List<Long> idsList);
	
	int deleteIdsPhysical(List<Long> idsList);
	
	int updateIdsRestore(List<Long> idsList);
}