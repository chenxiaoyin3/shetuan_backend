package com.grain.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.hongyu.Filter;
import com.hongyu.Filter.Operator;

public class FilterUtil{
	private FilterUtil(){
		
	}
	static public FilterUtil getInstance(){
		return new FilterUtil();
	}
	public List<Filter> getFilter(Object t){
		List<Filter> filters = new ArrayList<Filter>();
		Field[] fields = t.getClass().getDeclaredFields(); // 获取对象的所有属性对象：type，name，value
		for (Field fd : fields) {
			String name = fd.getName();
			if (name.equals("serialVersionUID"))
				continue;
			if (fd.getType().toString().equalsIgnoreCase("interface java.util.Set") ||
					fd.getType().toString().equalsIgnoreCase("interface java.util.List"))
				continue;
			Object value = getFieldValueByName(name, t);
			if (value != null ) {
				Filter fl= new Filter();
				fl.setProperty(name);
				fl.setValue(value);
				if (fd.getType().toString().equalsIgnoreCase("class java.lang.string")){
					if (((String)value).trim().equals("")) 
						continue;
					fl.setOperator(Operator.like);
				}
				else
					fl.setOperator(Operator.eq);
				filters.add(fl);
			}
		}
		return filters;
	}
	private Object getFieldValueByName(String fieldName, Object o) {  
	       try {    
	           String firstLetter = fieldName.substring(0, 1).toUpperCase();    
	           String getter = "get" + firstLetter + fieldName.substring(1);    
	           Method method = o.getClass().getMethod(getter, new Class[] {});    
	           Object value = method.invoke(o, new Object[] {});    
	           return value;    
	       } catch (Exception e) {    
	        //   log.error(e.getMessage(),e);    
	           return null;    
	       }    
	   } 

}
