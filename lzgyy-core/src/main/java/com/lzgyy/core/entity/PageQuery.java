package com.lzgyy.core.entity;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 分页查询工具类 <BR>
 * @param <T>
 */
@ToString
@EqualsAndHashCode
public class PageQuery<T> implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public PageQuery(){}
	
	public PageQuery(T queryParamObj){
		if (queryParamObj != null){
			this.setQueryParamObj(queryParamObj);
		}
	}
	
	/**
	 * 构造器
	 * @param queryParamObj 对象
	 * @param paramInt      int数组，0位置对应currPage当前页，1位置对应pageSize每页显示条数，默认0.10
	 */
	public PageQuery(T queryParamObj, int... paramInt){
		if (queryParamObj != null){
			this.setQueryParamObj(queryParamObj);
		}
		
		if (paramInt != null){
			if (paramInt[0] < 0){
				paramInt[0] = 0;
			}
			this.startPage =  paramInt[0];
			if(paramInt.length == 2){
				if (paramInt[1] < 0){
					paramInt[1] = 0;
				}
				this.limitSize =  paramInt[1];
			}
		}
	}
	
	/** 开始记录页数 **/
  	public int startPage = 0;
	
	/** 每页限制记录数 **/
	private int limitSize = 10;
	
	/** 查询条件对象 **/
	private T queryParamObj;
	
	public int getStartPage() {
		return startPage;
	}
	
	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}
	
	public int getLimitSize() {
		return limitSize;
	}
	
	public void setLimitSize(int limitSize) {
		this.limitSize = limitSize;
	}
	
	public void setQueryParamObj(T queryParamObj) {
		this.queryParamObj = queryParamObj;
	}
	
	public T getQueryParamObj() {
		return queryParamObj;
	}
	
}