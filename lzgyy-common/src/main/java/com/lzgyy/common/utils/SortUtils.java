package com.lzgyy.common.utils;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;  
import java.util.Comparator;  
import java.util.List;  

/**
 * @文件名   SortUtils.java 
 * @描述	     List对象排序的通用方法 
 * @作者     岳增晓 
 * @创建日期 2016年9月10日下午2:03:44 
 * @版本     v1.0
 */
@SuppressWarnings("all")
public class SortUtils<E>{ 
	
	/**
     * @Title: SortList 
     * @Description: 列表对象排序     例子:List<String/Integer...基本数据类型>
     * @param @param list
     * @param @param sort
     * @return void
     * @throws
     */
    public void SortList(List<E> list, final String sort){
    	
    	Collections.sort(list,new Comparator<E>(){ 
    		Collator clt = Collator.getInstance(java.util.Locale.CHINA); 
    		@Override 
    		public int compare(E o1, E o2) { 
    			if (sort != null && "asc".equals(sort.toLowerCase())) {
    				return clt.compare(o1, o2);
   			 	}else{
   			 		return -clt.compare(o1, o2);
   			 	} 
                 
            } 
        }); 
    }
    
    /**
     * @Title: SortList
     * @Description: 列表对象属性排序	例子:List<DemoSort>
     * @param @param list	 要排序的集合 
     * @param @param method  要排序的实体的属性所对应的get方法
     * @param @param sort    sort desc 为正序 
     * @return void
     * @throws
     */
    public void SortList(List<E> list, final String method, final String sort) {  
        // 用内部类实现排序  
        Collections.sort(list, new Comparator<E>() {  
        	Collator clt = Collator.getInstance(java.util.Locale.CHINA);
            public int compare(E a, E b) {  
                int ret = 0;  
                try {  
                    // 获取m1的方法名  
                    Method m1 = a.getClass().getMethod(method, null);  
                    // 获取m2的方法名  
                    Method m2 = b.getClass().getMethod(method, null);  
                      
                    if (sort != null && "desc".equals(sort.toLowerCase())) {  
  
                        //ret = m2.invoke(((E)b), null).toString().compareTo(m1.invoke(((E)a),null).toString());  
                    	//ret = processData(m2,b).compareTo(processData(m1,a));
                    	ret = clt.compare(processData(m2,b),processData(m1,a));
                    } else {  
                        // 正序排序  
                        //ret = m1.invoke(((E)a), null).toString().compareTo(m2.invoke(((E)b), null).toString());  
                    	//ret = processData(m1,a).compareTo(processData(m2,b));
                    	ret = clt.compare(processData(m1,a),processData(m2,b));
                    }  
                } catch (NoSuchMethodException ne) {  
                    System.out.println(ne);  
                } catch (IllegalArgumentException e) {  
                    e.printStackTrace();  
                }
                return ret;  
            }  
        });  
    }  
    
    private String processData(Method m,E e){
    	String val = "";
    	try {
    		val =  m.invoke(((E)e),null)==null?"":m.invoke(((E)e),null).toString();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		};
		return val;
    }
    
    
    public static void main(String[] args) {
    	
		List<DemoSort> dsList = new ArrayList<DemoSort>();
		DemoSort ds1 = new DemoSort();
		ds1.setName("你好");
		ds1.setTime("2015-12-12 12-12-12");
		DemoSort ds2 = new DemoSort();
		ds2.setTime("2015-12-12 12-12-15");
		ds2.setName("我好");
		DemoSort ds3 = new DemoSort();
		ds3.setTime("2015-12-12 12-12-11");
		ds3.setName("大家好");
		DemoSort ds4 = new DemoSort();
		ds4.setTime(null);
		ds4.setName("null");
		dsList.add(ds1);
		dsList.add(ds2);
		dsList.add(ds3);
		dsList.add(ds4);
		
		System.out.println("******************** 排序前 ********************");
		for(DemoSort ds: dsList){
			System.out.println(ds.getName()+ "    "+ds.getTime());
		}
		System.out.println("******************** 时间排序后 ********************");
		
		SortUtils<DemoSort> sortUtils= new SortUtils<DemoSort>();  
		sortUtils.SortList(dsList, "getTime", "desc");
		
		for(DemoSort ds: dsList){
			System.out.println(ds.getName()+ "    "+ds.getTime());
		}
		
		System.out.println("******************** 名称排序后 ********************");
		
		//SortUtils<DemoSort> sortUtils= new SortUtils<DemoSort>();
		sortUtils.SortList(dsList, "getName", "asc");
		
		for(DemoSort ds: dsList){
			System.out.println(ds.getName()+ "    "+ds.getTime());
		}
    	
		System.out.println("******************** === ********************");
    	List<String> strList = new ArrayList<String>();
    	strList.add("你好");
    	strList.add("我好");
    	strList.add("z");
    	strList.add("a");
    	strList.add("大家好");
    	SortUtils<String> sortUtils2= new SortUtils<String>();
    	sortUtils2.SortList(strList,"desc");
    	for(String str : strList){
    		System.out.println(str);
    	}
	}

}

/**
 * 测试例子
 */
class DemoSort{
	private String id;
	private String name;
	private String time;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
