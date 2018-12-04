package com.lzgyy.platform.modules.demo.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lzgyy.core.entity.PageResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/demo")
@Api(value = "测试例子接口",description = "提供对测试操作服务接口（增、删、改、查）")
public class DemoController {

	@GetMapping(value = "/v1.0/get")
	@ApiOperation(value = "获取测试信息", notes = "获取测试的信息", httpMethod = "GET")
    public @ResponseBody Object get(HttpServletRequest request,
    								HttpServletResponse response) {
       
        return new PageResult<Object>("你好呀，骚年");
    }
	
	@GetMapping(value = "/v1.0/index1")
	@ApiOperation(value = "get根据测试id，返回数据",notes = "get根据测试id，返回数据", httpMethod = "GET")
	@ApiImplicitParam(paramType = "query", name = "id", required = true, value = "测试ID", dataType = "string")
    public @ResponseBody Object index1(HttpServletRequest request,
    								   HttpServletResponse response,
    								   @RequestParam("id") String id) {
       
		PageResult<Object> pageResult = new PageResult<Object>();
		pageResult.setData("get 你好 "+id);
        return pageResult;
    }
	
	@GetMapping(value = "/v1.0/index2/{id}")
	@ApiOperation(value = "get根据测试id，返回数据",notes = "get根据测试id，返回数据", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", name = "id", required = true, value = "测试ID", dataType = "string")
    public @ResponseBody Object index2(HttpServletRequest request,
    								   HttpServletResponse response,
    								   @PathVariable("id") String id) {
       
		PageResult<Object> pageResult = new PageResult<Object>();
		pageResult.setData("get 你好 "+id);
        return pageResult;
    }
	
	@PostMapping(value = "/v1.0/index3")
	@ApiOperation(value = "post根据测试id，返回数据", notes = "post根据测试id，返回数据", httpMethod = "POST")
	@ApiImplicitParam(paramType = "body", name = "str", required = true, value = "测试内容", dataType = "string")
    public @ResponseBody Object index3(HttpServletRequest request,
    								   HttpServletResponse response,
    								   @RequestBody String str) {
       
		PageResult<Object> pageResult = new PageResult<Object>();
		pageResult.setData("post 你好 "+str);
        return pageResult;
    }
}