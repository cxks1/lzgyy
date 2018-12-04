package com.lzgyy.common.utils;

import javax.servlet.http.HttpServletRequest;

import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.DeviceType;
import nl.bitwalker.useragentutils.UserAgent;

/**
 * 用户代理字符串识别工具
 * @author ThinkGem
 * @version 2014-6-13
 */
public class UserAgentUtils {

	/**
	 * 获取用户代理对象
	 * @param request
	 * @return
	 */
	public static UserAgent getUserAgent(HttpServletRequest request){
		return UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
	}
	
	/**
	 * 获取设备类型
	 * @param request
	 * @return
	 */
	public static DeviceType getDeviceType(HttpServletRequest request){
		return getUserAgent(request).getOperatingSystem().getDeviceType();
	}
	
	/**
	 * 是否是PC
	 * @param request
	 * @return
	 */
	public static boolean isComputer(HttpServletRequest request){
		return DeviceType.COMPUTER.equals(getDeviceType(request));
	}

	/**
	 * 是否是手机
	 * @param request
	 * @return
	 */
	public static boolean isMobile(HttpServletRequest request){
		return DeviceType.MOBILE.equals(getDeviceType(request));
	}
	
	/**
	 * 是否是平板
	 * @param request
	 * @return
	 */
	public static boolean isTablet(HttpServletRequest request){
		return DeviceType.TABLET.equals(getDeviceType(request));
	}

	/**
	 * 是否是手机和平板
	 * @param request
	 * @return
	 */
	public static boolean isMobileOrTablet(HttpServletRequest request){
		DeviceType deviceType = getDeviceType(request);
		return DeviceType.MOBILE.equals(deviceType) || DeviceType.TABLET.equals(deviceType);
	}
	
	/**
	 * 获取浏览类型
	 * @param request
	 * @return
	 */
	public static Browser getBrowser(HttpServletRequest request){
		return getUserAgent(request).getBrowser();
	}
	
	/**
	 * 是否IE版本是否小于等于IE8
	 * @param request
	 * @return
	 */
	public static boolean isLteIE8(HttpServletRequest request){
		Browser browser = getBrowser(request);
		return Browser.IE5.equals(browser) || Browser.IE6.equals(browser)
				|| Browser.IE7.equals(browser) || Browser.IE8.equals(browser);
	}
	
	//========================自定义方法=====================
	
	/**
	 * 是否微信浏览器
	 * @param request
	 * @return
	 */
	public static boolean isWechat(HttpServletRequest request) {
		String ua = request.getHeader("User-Agent").toLowerCase();
		if (ua.toLowerCase().indexOf("micromessenger") > -1) {
			// 微信
			return true;
		}
		// 非微信手机浏览器
		return false;
	}
	
	/**
	 * 是否支付宝浏览器
	 * @param request
	 * @return
	 */
	public static boolean isAlipay(HttpServletRequest request) {
		String ua = request.getHeader("User-Agent").toLowerCase();
		if (ua.toLowerCase().indexOf("alipayclient") > -1) {
			// 支付宝
			return true;
		}
		// 非支付宝手机浏览器
		return false;
	}
}