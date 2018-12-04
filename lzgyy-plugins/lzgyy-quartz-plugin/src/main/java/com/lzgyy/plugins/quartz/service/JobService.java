package com.lzgyy.plugins.quartz.service;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lzgyy.plugins.quartz.mapper.JobMapper;
import com.lzgyy.core.constant.Const;
import com.lzgyy.plugins.quartz.entity.JobEntity;
import com.lzgyy.plugins.quartz.job.DynamicJob;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JobService {

    @Autowired
    private JobMapper jobMapper;

    //通过Id获取Job
    public JobEntity getJobEntityById(Long id) {
    	Map<String,Object> paramMap = new HashMap<String, Object>();
    	paramMap.put("id", id);
		paramMap.put(Const.PAGE_STARTSIZE, 0);
		paramMap.put(Const.PAGE_LIMITSIZE, 10000);
		List<JobEntity> jobEntityList = jobMapper.getList(paramMap);
		if (jobEntityList != null && jobEntityList.size() > 0) {
			return jobEntityList.get(0);
		}
		return null;
    }

    //从数据库中加载获取到所有Job
    public List<JobEntity> loadJobs() {
        Map<String,Object> paramMap = new HashMap<String, Object>();
		paramMap.put(Const.PAGE_STARTSIZE, 0);
		paramMap.put(Const.PAGE_LIMITSIZE, 10000);
		return jobMapper.getList(paramMap);
    }

    //获取JobDataMap.(Job参数对象)
    public JobDataMap getJobDataMap(JobEntity job) {
        JobDataMap map = new JobDataMap();
        map.put("name", job.getName());
        map.put("group", job.getGroup());
        map.put("cronExpression", job.getCron());
        map.put("parameter", job.getParameter());
        map.put("JobDescription", job.getDescription());
        map.put("vmParam", job.getVmParam());
        map.put("jarPath", job.getJarPath());
        map.put("status", job.getStatus());
        return map;
    }

    //获取JobDetail,JobDetail是任务的定义,而Job是任务的执行逻辑,JobDetail里会引用一个Job Class来定义
    public JobDetail geJobDetail(JobKey jobKey, String description, JobDataMap map) {
        return JobBuilder.newJob(DynamicJob.class)
                .withIdentity(jobKey)
                .withDescription(description)
                .setJobData(map)
                .storeDurably()
                .build();
    }

    //获取Trigger (Job的触发器,执行规则)
    public Trigger getTrigger(JobEntity job) {
        return TriggerBuilder.newTrigger()
                .withIdentity(job.getName(), job.getGroup())
                .withSchedule(CronScheduleBuilder.cronSchedule(job.getCron()))
                .build();
    }

    //获取JobKey,包含Name和Group
    public JobKey getJobKey(JobEntity job) {
        return JobKey.jobKey(job.getName(), job.getGroup());
    }
}