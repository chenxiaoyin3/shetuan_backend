package com.hongyu.util.industrialBankUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期时间工具类
 * @author GSbing
 * @version 1.0.0
 * @date 2018-09-04
 */
public class DateTimeUtil {

	public static String getDate() {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		return formatter.format(new Date());
	}
	
	public static String getDateTime() {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		return formatter.format(new Date());
	}
}
