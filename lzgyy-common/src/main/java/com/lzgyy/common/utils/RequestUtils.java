package com.lzgyy.common.utils;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public final class RequestUtils {

    /**
     * <B>构造方法</B><BR>
     */
    private RequestUtils() {
    }
    
    /**
     * <B>方法名称：</B>获取客户端信息<BR>
     * <B>概要说明：</B>根据请求获取客户端信息。<BR>
     * 
     * @param request 请求
     * @return String 客户端信息
     */
    public static String getClientInfo(HttpServletRequest request) {
    	String ip = request.getHeader("X-Real-IP");
    	if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
    		ip = request.getHeader("x-forwarded-for");
    	}
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * <B>方法名称：</B>判断客户端是否为Firefox<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param request 请求
     * @return boolean 是否为Firefox
     */
    public static boolean isFirefox(HttpServletRequest request) {
        String agent = request.getHeader("USER-AGENT").toUpperCase();
        return (!StringUtils.isBlank(agent) && agent.indexOf("FIREFOX") > -1);
    }

    /**
     * <B>方法名称：</B>获取上传文件<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param request 请求
     * @return MultipartFile 上传文件
     */
    public static MultipartFile getUploadFile(HttpServletRequest request) {
        return getUploadFile(request, "file");
    }

    /**
     * <B>方法名称：</B>获取上传文件<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param request 请求
     * @param name 文件名
     * @return MultipartFile 上传文件
     */
    public static MultipartFile getUploadFile(HttpServletRequest request, String name) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        return multipartRequest.getFile(name);
    }

    /**
     * <B>方法名称：</B>保存上传文件<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param request 请求
     * @param path 保存路径
     * @throws IOException 预想外异常错误
     */
    public static void saveUploadFile(HttpServletRequest request, String path) throws IOException {
        MultipartFile file = getUploadFile(request);
        String sep = System.getProperty("file.separator");
        File filePath = new File(path);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        String fullPath = path + sep + file.getOriginalFilename();
        File uploadedFile = new File(fullPath);
        file.transferTo(uploadedFile);
    }

}
