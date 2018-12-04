package com.lzgyy.platform.modules.test.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.lzgyy.core.constant.Const;
import com.lzgyy.core.entity.PageQuery;
import com.lzgyy.core.entity.PageResult;
import com.lzgyy.platform.modules.test.entity.Test;
import com.lzgyy.platform.modules.test.service.TestService;

@Controller
@RequestMapping("/test")
public class TestController {

	@Resource
	private TestService testService;
	
	@GetMapping(value = "/v1.0/index")
    public ModelAndView index1(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView ret = new ModelAndView();
        ret.setViewName("/demo/index");
        return ret;
    }
	
	/**
     * <B>方法名称：</B>测试首页<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param request 页面请求
     * @param response 页面响应
     * @param dataYear 年份
     * @return ModelAndView 模型视图
     */
	@GetMapping(value = "/v1.0/index.html")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView ret = new ModelAndView();
        ret.setViewName("/demo/index");
        return ret;
    }
    
	@GetMapping(value = "/v1.0/getDemoList.json")
    public void getDemoList(HttpServletRequest request, HttpServletResponse response){
    	System.out.println(">>>>>>>>>>>>>>>> 进入getDemoList方法 <<<<<<<<<<<<<<<<<<");
    	List<Test> testList = null;
    	Integer total = 0;
		try {
			Test test = new Test();
			test.setDeleteState("1");
			testList = testService.getList(new PageQuery<Test>(test, 0, 2));
			total = testService.getTotal(test);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		PageResult pageResult = new PageResult();
		pageResult.setData(testList);
		pageResult.setStartPage(0);
		pageResult.setLimitSize(10);
		pageResult.setTotal(total);
		
		JSONObject json = new JSONObject();
		String str = json.toJSONString(pageResult);
		response.setContentType("text/plain;charset=" + Const.CHARSET_UTF8);
		response.setCharacterEncoding(Const.CHARSET_UTF8);
		try {
			response.getWriter().write(str);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		System.out.println("++++++++++++++++++++ 离开getDemoList方法 +++++++++++++");
    }
    
	@GetMapping(value = "/v1.0/getDemoList2.json")
    public void getDemoList2(HttpServletRequest request, HttpServletResponse response){
    	System.out.println(">>>>>>>>>>>>>>>> 进入getDemoList方法 <<<<<<<<<<<<<<<<<<");
    	List<Test> testList = null;
		try {
			Test test = new Test();
			test.setDeleteState("1");
			testList = testService.getList2(test, 0, 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		JSONObject json = new JSONObject();
		String str = json.toJSONString(testList);
		response.setContentType("text/plain;charset=" + Const.CHARSET_UTF8);
		response.setCharacterEncoding(Const.CHARSET_UTF8);
		try {
			response.getWriter().write(str);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		System.out.println("++++++++++++++++++++ 离开getDemoList方法 +++++++++++++");
    }

}
