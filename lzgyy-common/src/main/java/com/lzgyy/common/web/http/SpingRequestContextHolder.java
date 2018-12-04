package com.lzgyy.common.web.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SpingRequestContextHolder extends RequestContextHolder{

	/**
	 * 获取应用上下文HttpSession
	 * @Title: getSession 
	 * @param @return    设定文件 
	 * @return HttpSession    返回类型 
	 * @throws
	 */
	public static HttpSession  getHttpSession(){
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return request.getSession();
	}
	
	/**
	 * 获取应用上下文HttpServletRequest
	 * @Title: getHttpRequest 
	 * @param @return    设定文件 
	 * @return HttpServletRequest   返回类型 
	 * @throws
	 */
	public static HttpServletRequest  getHttpRequest(){
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return request;
	}
	
	/**
	 * 获取应用上下文HttpServletResponse
	 * @Title: getHttpResponse
	 * @param @return    设定文件 
	 * @return HttpServletResponse   返回类型 
	 * @throws
	 */
	public static HttpServletResponse  getHttpResponse(){
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
		return response;
	}
	
}