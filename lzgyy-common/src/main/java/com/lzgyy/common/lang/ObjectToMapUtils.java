package com.lzgyy.common.lang;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * 对象转Map集合工具类 <BR>
 */
public class ObjectToMapUtils {

	/** JAVA基础类型前缀 **/
	private static final String JAVA_BASIC_TYPE_PREFIX = "java.";
	/** JAVA日期类型 **/
	private static final String JAVA_DATE_TYPE = "java.util.Date";
	/** 默认日期格式字符串 **/
	private static final String DEFAULT_DATE_FORMAT_STR = "YYYY-MM-dd HH:mm:ss";
	/** 默认NULL值忽略标识 **/
	private static final boolean DEFAULT_NULL_IGNORE_MARK = false;

	//================= 默认null为空 ==================
	/*public static Map<String, Object> objectToMap(Object obj) throws IllegalAccessException {
		return objectToMap(obj, null);
	}
	public static Map<String, Object> objectToMap(Object obj, String... excludeFields) throws IllegalAccessException {
		return objectToMap(obj, null, excludeFields);
	}
	*//**
	 * 利用递归调用将Object中的值全部进行获取
	 * @param obj           对象
	 * @param timeFormatStr 格式化时间字符串默认<strong>YYYY-MM-dd HH:mm:ss</strong>
	 * @param excludeFields 排除的属性
	 * @return
	 * @throws IllegalAccessException
	 *//*
	public static Map<String, Object> objectToMap(Object obj, String timeFormatStr, String... excludeFields) throws IllegalAccessException {

		Map<String, Object> map = new HashMap<>();
		if (excludeFields != null && excludeFields.length != 0){
			List<String> list = Arrays.asList(excludeFields);
			objectTransfer(obj, timeFormatStr, map, false, list);
		}else {
			objectTransfer(obj, timeFormatStr, map, false, null);
		}
		return map;
	}*/
	
	public static Map<String, Object> objectToIgnorNullMergeMap(Object obj) throws IllegalAccessException{
		return objectToIgnorNullMergeMap(obj, null);
	}
	
	public static Map<String, Object> objectToIgnorNullMergeMap(Object obj, String... excludeFields) throws IllegalAccessException {
		return objectToIgnorNullMergeMap(obj, null, excludeFields);
	}
	
	/**
	 * 利用递归调用将Object中的值全部进行获取存入Map集合<BR>
	 * 忽略null值合并map
	 * @param obj           对象
	 * @param timeFormatStr 格式化时间字符串默认<strong>YYYY-MM-dd HH:mm:ss</strong>
	 * @param excludeFields 排除的属性
	 * @return
	 * @throws IllegalAccessException
	 */
	public static Map<String, Object> objectToIgnorNullMergeMap(Object obj, String timeFormatStr, String... excludeFields) throws IllegalAccessException {

		Map<String, Object> map = new HashMap<>();
		if (excludeFields != null && excludeFields.length != 0){
			List<String> list = Arrays.asList(excludeFields);
			objectTransfer(obj, timeFormatStr, map, true, true, list);
		}else {
			objectTransfer(obj, timeFormatStr, map, true, true, null);
		}
		return map;
	}

	/**
	 * 递归调用函数
	 * @param obj            对象
	 * @param map            map
	 * @param mergeMapMark   合并Map标识
	 * @param nullIgnoreMark NULL值忽略标识(true/false  忽略/空值替换)
	 * @param excludeFields  排除字段
	 * @return
	 * @throws IllegalAccessException
	 */
	private static Map<String, Object> objectTransfer(Object obj, String timeFormatStr, Map<String, Object> map, boolean mergeMapMark, boolean nullIgnoreMark, List<String> excludeFields) throws IllegalAccessException {

		boolean isExclude=false;
		//默认字符串
		String formatStr = DEFAULT_DATE_FORMAT_STR;
		//设置格式化字符串
		if (timeFormatStr != null && !timeFormatStr.isEmpty()) {
			formatStr = timeFormatStr;
		}
		if (excludeFields != null){
			isExclude = true;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
		Class<?> clazz = obj.getClass();
		//获取值
		for (Field field : clazz.getDeclaredFields()) {
			//String fieldName = clazz.getSimpleName() + "." + field.getName();
			String fieldName = field.getName();
			//判断是不是需要跳过某个属性
			if (isExclude && excludeFields.contains(fieldName)){
				continue;
			}
			//设置属性可以被访问
			field.setAccessible(true);
			Object value = field.get(obj);
			if (value == null){
				if(nullIgnoreMark){
					continue;
				}
				value = "";
			}
			Class<?> valueClass = value.getClass();
			if (valueClass.isPrimitive()) {
				map.put(fieldName, value);

			} else if (valueClass.getName().contains(JAVA_BASIC_TYPE_PREFIX)) { //判断是不是基本类型
				if (valueClass.getName().equals(JAVA_DATE_TYPE)) {
					//格式化Date类型
					java.util.Date date = (java.util.Date) value;
					String dataStr = sdf.format(date);
					map.put(fieldName, dataStr);
				} else {
					map.put(fieldName, value);
				}
			} else {
				
				if(mergeMapMark){
					// 一个map
					objectTransfer(value, timeFormatStr, map, mergeMapMark, nullIgnoreMark, excludeFields);
				}else{
					// 多个map
					Map<String,Object> map2 = new HashMap<String,Object>();
					objectTransfer(value, timeFormatStr, map2, mergeMapMark, nullIgnoreMark, excludeFields);
					map.put(fieldName, map2);
				}
			}
		}
		return map;
	}

	public static void main(String[] args) {
		Demo demo = new Demo();
			demo.setId(new Long(1));
			demo.setName("你好");
			demo.setTime(new java.util.Date());

			Test test = new Test();
			test.setTestId(new Long(11));
			test.setTestName("内层好");
			//test.setTestTime(new java.util.Date());
			demo.setTest(test);
			
			T t = new T();
			t.setT1("111");
			demo.setT(t);
		//List<Demo> demo = new ArrayList<Demo>();
		//int demo = 0;
		try {
			Map<String,Object> objMap = objectToIgnorNullMergeMap(demo);
			System.out.println(objMap);

			System.out.println("======================================");

			Map<String,Object> objMap2 = objectToIgnorNullMergeMap(demo, new String[]{"name"});
			//Map<String,String> objMap3 = objectToMapString(null, demo);
			System.out.println(objMap2);
			//System.out.println(objMap3);
			System.out.println(new JSONObject().toJSONString(objMap2));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}

class Demo{
	private Long id;
	private String name;
	private BigDecimal ss;
	private java.util.Date time;
	private Object t;
	
	private Test test;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getSs() {
		return ss;
	}

	public void setSs(BigDecimal ss) {
		this.ss = ss;
	}

	public java.util.Date getTime() {
		return time;
	}

	public void setTime(java.util.Date time) {
		this.time = time;
	}

	public Object getT() {
		return t;
	}

	public void setT(Object t) {
		this.t = t;
	}

	public Test getTest() {
		return test;
	}

	public void setTest(Test test) {
		this.test = test;
	}
}

class Test{
	private Long testId;
	private String testName;
	private java.util.Date testTime;
	public Long getTestId() {
		return testId;
	}
	public void setTestId(Long testId) {
		this.testId = testId;
	}
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	public java.util.Date getTestTime() {
		return testTime;
	}
	public void setTestTime(java.util.Date testTime) {
		this.testTime = testTime;
	}
}
class T{
	private String t1;
	private String t2;
	public String getT1() {
		return t1;
	}
	public void setT1(String t1) {
		this.t1 = t1;
	}
	public String getT2() {
		return t2;
	}
	public void setT2(String t2) {
		this.t2 = t2;
	}
}