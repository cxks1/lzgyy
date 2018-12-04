package com.lzgyy.common.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public final class ResponseUtils {

    /**
     * <B>构造方法</B><BR>
     */
    private ResponseUtils() {
    }

    /**
     * <B>方法名称：</B>设定HTML文本响应<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param response 响应
     * @param html HTML文本
     */
    public static void putHtmlResponse(HttpServletResponse response, String html) {
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write(html);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <B>方法名称：</B>设定文本响应<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param response 响应
     * @param text 文本信息
     */
    public static void putTextResponse(HttpServletResponse response, String text) {
        response.setContentType("text/plain;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write(text);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <B>方法名称：</B>设定JSON对象响应<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param response 响应
     * @param json JSON对象
     */
    public static void putJsonResponse(HttpServletResponse response, JSONObject json) {
        if (json == null) {
            putTextResponse(response, "{}");
        }
        else {
            putTextResponse(response, json.toString());
        }
    }

    /**
     * <B>方法名称：</B>设定JSON对象响应<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param response 响应
     * @param jsonList JSON列表
     */
    public static void putJsonResponse(HttpServletResponse response, List<JSONObject> jsonList) {
        if (jsonList == null || jsonList.size() < 1) {
            putTextResponse(response, "[]");
        }
        else {
            putJsonResponse(response, new JSONArray(FastJsonConvert.convertJSONToArray(jsonList.toString(), Object.class)));
        }
    }

    /**
     * <B>方法名称：</B>设定JSON对象响应<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param response 响应
     * @param jsonArray JSON数组
     */
    public static void putJsonResponse(HttpServletResponse response, JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.size() < 1) {
            putTextResponse(response, "[]");
        }
        else {
            putTextResponse(response, jsonArray.toString());
        }
    }

    /**
     * <B>方法名称：</B>设定JSON对象响应<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param response 响应
     */
    public static void putJsonSuccessResponse(HttpServletResponse response) {
        putJsonSuccessResponse(response, null);
    }

    /**
     * <B>方法名称：</B>设定JSON对象响应<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param response 响应
     * @param json JSON
     */
    public static void putJsonSuccessResponse(HttpServletResponse response, JSONObject json) {
        JSONObject ret = new JSONObject();
        try {
            ret.put("success", true);
            ret.put("data", json);
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
        putTextResponse(response, ret.toString());
    }

    /**
     * <B>方法名称：</B>设定JSON对象错误响应<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param response 响应
     * @param message 错误消息
     */
    public static void putJsonFailureResponse(HttpServletResponse response, String message) {
        JSONObject ret = new JSONObject();
        try {
            ret.put("success", false);
            ret.put("message", message);
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
        putTextResponse(response, ret.toString());
    }

    /**
     * <B>方法名称：</B>设定文件响应<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param request 请求
     * @param response 响应
     * @param fileName 文件名称
     */
    public static void putFileResponse(HttpServletRequest request, HttpServletResponse response, String fileName) {
        putFileResponse(request, response, fileName, null, "UTF-8");
    }

    /**
     * <B>方法名称：</B>设定文件响应<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param request 请求
     * @param response 响应
     * @param fileName 文件名称
     * @param inline 是否内嵌
     */
    public static void putFileResponse(HttpServletRequest request, HttpServletResponse response, String fileName,
            Boolean inline) {
        putFileResponse(request, response, fileName, inline, "UTF-8");
    }

    /**
     * <B>方法名称：</B>设定文件响应<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param request 请求
     * @param response 响应
     * @param fileName 文件名称
     * @param inline 是否内嵌
     * @param charset 字符集
     */
    public static void putFileResponse(HttpServletRequest request, HttpServletResponse response, String fileName,
            Boolean inline, String charset) {

        int i = fileName.lastIndexOf(".") + 1;
        String ext = null;
        if (i > 0 && i < fileName.length()) {
            ext = fileName.substring(i).toLowerCase();
        }

        try {
            if (RequestUtils.isFirefox(request)) {
                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            }
            else {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            }
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        String type = (inline != null && inline) ? "inline" : "attachment";

        String mime = "application/octet-stream";
        if (StringUtils.isBlank(ext)) {
        }
        else if (ext.equals("txt")) {
            mime = "text/plain";
        }
        else if (ext.equals("htm")) {
            mime = "text/htm";
        }
        else if (ext.equals("html")) {
            mime = "text/html";
        }
        else if (ext.equals("jpg")) {
            mime = "image/jpeg";
        }
        else if (ext.equals("jpe")) {
            mime = "image/jpeg";
        }
        else if (ext.equals("jpeg")) {
            mime = "image/jpeg";
        }
        else if (ext.equals("png")) {
            mime = "image/png";
        }
        else if (ext.equals("gif")) {
            mime = "image/gif";
        }
        else if (ext.equals("bmp")) {
            mime = "image/bmp";
        }
        else if (ext.equals("tif")) {
            mime = "image/tiff";
        }
        else if (ext.equals("tiff")) {
            mime = "image/tiff";
        }
        else if (ext.equals("svg")) {
            mime = "image/svg+xml";
        }
        else if (ext.equals("pdf")) {
            mime = "application/pdf";
        }
        else if (ext.equals("doc")) {
            mime = "application/msword";
        }
        else if (ext.equals("docx")) {
            mime = "application/msword";
        }
        else if (ext.equals("xls")) {
            mime = "application/vnd.ms-excel";
        }
        else if (ext.equals("xlsx")) {
            mime = "application/vnd.ms-excel";
        }
        else if (ext.equals("ppt")) {
            mime = "application/vnd.ms-powerpoint";
        }
        else if (ext.equals("pptx")) {
            mime = "application/vnd.ms-powerpoint";
        }
        else if (ext.equals("mpp")) {
            mime = "application/vnd.ms-project";
        }
        else if (ext.equals("mppx")) {
            mime = "application/vnd.ms-project";
        }
        else if (ext.equals("mp4")) {
            mime = "video/mp4";
        }

        response.setContentType(mime + ";charset=" + charset);
        response.setCharacterEncoding(charset);
        response.setHeader("Cache-Control", "no-store");
        response.addDateHeader("Last-Modified", System.currentTimeMillis());
        response.addHeader("Content-Disposition", type + ";filename=" + fileName);
    }
  
}
