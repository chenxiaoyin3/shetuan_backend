package com.hongyu.util.liyang;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtilsBean;
/**
 * 拷贝Bean的所有非空属性值
 * @author liyang
 *
 */
public class CopyFromNotNullBeanUtilsBean extends BeanUtilsBean{

	@Override
	public void copyProperty(Object arg0, String arg1, Object arg2)
			throws IllegalAccessException, InvocationTargetException {
		if(arg2==null){
			return ;
		}
		super.copyProperty(arg0, arg1, arg2);
	}

}
