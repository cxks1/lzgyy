package com.lzgyy.core.entity;

import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import com.lzgyy.common.lang.ObjectToMapUtils;
import com.lzgyy.core.constant.Const;

/**
 * 分页查询帮助工具类 <BR>
 * @param <T>
 */
public class PageQueryHelp{
	
	public static Map<String, Object> getParamMap(PageQuery<?> pageQuery) {
		
		if (pageQuery == null){
			return null;
		}
		
		Map<String, Object> paramMap = null;
		try {
			paramMap = ObjectToMapUtils.objectToIgnorNullMergeMap(pageQuery.getQueryParamObj());
			if (paramMap != null){
				// 开始记录行数
				paramMap.put(Const.PAGE_STARTSIZE, (pageQuery.getStartPage()== 0?0:pageQuery.getStartPage()-1)*pageQuery.getLimitSize());
				// 每页限制记录数
				paramMap.put(Const.PAGE_LIMITSIZE, pageQuery.getLimitSize());
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return paramMap;
	}
	
	public static Integer[] getArrStartLimitSize(JSONObject json) {
		
		Integer[] intArr = new Integer[]{0,10};
		
		if (json != null){
			if (json.get(Const.PAGE_STARTPAGE) != null && json.get(Const.PAGE_STARTPAGE) instanceof Integer &&
				json.get(Const.PAGE_LIMITSIZE) != null && json.get(Const.PAGE_LIMITSIZE) instanceof Integer){
				
				intArr[0] = (json.getInteger(Const.PAGE_STARTPAGE)==0?0:json.getInteger(Const.PAGE_STARTPAGE)-1)*json.getInteger(Const.PAGE_LIMITSIZE);
				intArr[1] = json.getInteger(Const.PAGE_LIMITSIZE);
			}
			
			json.remove(Const.PAGE_STARTPAGE);
			json.remove(Const.PAGE_LIMITSIZE);
		}
		return intArr;
	}
	
	public static Integer getStartSize(Integer startPage, Integer limitSize){
		if (startPage != null && limitSize != null){
			return startPage == 0 ? startPage : (startPage -1) * limitSize;
		}
		return 0;
	}
}