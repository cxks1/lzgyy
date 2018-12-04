package com.lzgyy.core.entity;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 分页结果工具类 <BR>
 * @param <T>
 */
@ToString
@EqualsAndHashCode
public class PageResult<T> {
	
	public PageResult(){}
	
	public PageResult(T data){
		
		if (data != null){
			this.setData(data);;
		}
	}
	
	/** 开始记录页数 **/
  	public int startPage;
  	
  	/** 每页限制记录数 **/
  	public int limitSize;
  	
  	/** 记录总页数 */
  	public int totalPage;
  	
  	/** 记录总数 */
  	public int total;
  	
  	/** 返回数据 */
  	public T data;

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

	public int getTotalPage() {
		
		if (this.limitSize == 0 || this.total == 0){
			return 0;
		}
		if (this.total % limitSize == 0){
			this.totalPage = this.total / this.limitSize;
		} else {
			this.totalPage = this.total / this.limitSize + 1;
		}
		return this.totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
  	
}
