package com.hongyu.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import org.activiti.engine.impl.pvm.runtime.StartingExecution;
import org.apache.commons.lang.time.DateUtils;

public class DateUtil {
	/**
	 * 主要用于投保到江泰是提供时间信息
	 * 时间格式是yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getSimpleDate(Date dt){
		DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //HH表示24小时制；  
        String formatDate = dFormat.format(dt); 
        return formatDate;
	}
	/**
	 * 获取当前时间的两个小时之后的时间
	 * 为了江泰投保用
	 * @param date
	 * @return
	 */
	public static String getTimeAfterTwoHours(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		/* HOUR_OF_DAY 指示一天中的小时 */
		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) +2 );
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String time = df.format(calendar.getTime());
//		System.out.println("两个小时后的时间：" + df.format(calendar.getTime()));
//		System.out.println("当前的时间：" + df.format(new Date()));
		return time;
	}
	public static Date getDateAfterTwoHours(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		/* HOUR_OF_DAY 指示一天中的小时 */
		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) +2 );
		return calendar.getTime();
	}
	/**
	 * 主要用于投保到江泰是提供时间信息
	 * 时间格式是yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getSimpleEndOfDayDate(Date dt){
		DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //HH表示24小时制；  
        String formatDate = dFormat.format(getEndOfDay(dt)); 
        return formatDate;
	}
	/**
	 * 主要用于投保到江泰是提供时间信息
	 * 时间格式是yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getSimpleStartOfDayDate(Date dt){
		DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //HH表示24小时制；  
        Date date = getStartOfDay(dt);
		String formatDate = dFormat.format(date); 
        return formatDate;
	}

	/**
	 * 主要用于投保到江泰是提供时间信息
	 * 时间格式是yyyy-MM-dd
	 * @return
	 */
	public static String getBirthday(Date dt) {
		DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd"); //HH表示24小时制；  
        String formatDate = dFormat.format(dt); 
        return formatDate;
	}
	/**
	 * 主要用于投保到江泰时组装流水号后缀的时间信息
	 * 时间格式是MMddHHmmss
	 * @return
	 */
	public static String getSuffixDate(Date dt){
		DateFormat dFormat = new SimpleDateFormat("MMddHHmmss"); //HH表示24小时制；  
        String formatDate = dFormat.format(dt); 
        return formatDate;
	}
	/**
	 * 用于生成合同的文件名
	 * yyyyMMddHHmmss
	 * @param dt
	 * @return
	 */
	public static String getfileDate(Date dt){
		DateFormat dFormat = new SimpleDateFormat("yyyyMMddHHmmss"); //HH表示24小时制；  
        String formatDate = dFormat.format(dt); 
        return formatDate;
	}
	public static Date getEndOfDay(Date date) {
        return DateUtils.addSeconds(DateUtils.ceiling(date, Calendar.DATE), -1);
    }

    public static Date getStartOfDay(Date date) {
        return DateUtils.truncate(date, Calendar.DATE);
    }
    public static Date getNextDay(Date date){
    	Calendar calendar = new GregorianCalendar();
    	calendar.setTime(date);
    	calendar.add(Calendar.DATE, 1);
    	date = calendar.getTime();
    	return date;
    }
    public static Date getPreDay(Date date){
    	Calendar calendar = new GregorianCalendar();
    	calendar.setTime(date);
    	calendar.add(Calendar.DATE, -1);
    	date = calendar.getTime();
    	return date;
    }
    
    public static Date getDateAfterSpecifiedDays(Date date, Integer days) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	calendar.add(calendar.DATE, days);
    	return calendar.getTime();
    }
    
    public static Date getDateAfterSpecifiedHours(Date date,Integer hours){
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	calendar.add(calendar.HOUR_OF_DAY, hours);
    	return calendar.getTime();
    }
    /**
     * 根据当前日期向后的天数，返回最终日期
     * 日期格式是时间格式是yyyy-MM-dd
     * @param date
     * @param days
     * @return
     */
    public static Date getDateAfterSpecifiedDaysFormat(Date date, Integer days) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	calendar.add(calendar.DATE, days);
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    	String endDate = df.format(calendar.getTime());
    	Date enDate = null;
    	try {
			enDate = df.parse(endDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return enDate;
    }

    /**
     * Date b - Date a
     * @param a
     * @param b
     * @return
     */
    public static Long getDaysBetweenTwoDates(Date a, Date b) {
    	Long diffs = b.getTime() - a.getTime();
    	return TimeUnit.DAYS.convert(diffs, TimeUnit.MILLISECONDS);
    }
    public static String getNextMonth(String date) throws ParseException{
	 	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM");  
    	Date dt=sdf.parse(date);
    	Calendar rightNow = Calendar.getInstance();
    	rightNow.setTime(dt);
    	rightNow.add(Calendar.MONTH,1);//日期加1个月
    	Date nextMonth=rightNow.getTime();
    	String reStr = sdf.format(nextMonth);
    	return reStr;
    }
    public static Integer getAgeByBirthday(Date date){
    	if(date==null)
    		return 0;
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	int birth = calendar.get(Calendar.YEAR);
    	calendar.setTime(new Date());
    	int now = calendar.get(Calendar.YEAR);
    	return now - birth;
    }
    /**
     * 从身份证中获去出生日期
     * @param certificate 身份证号码
     * @return
     * @throws Exception 
     */
    public static Date getBirthdayByShenfenzheng(String certificate) throws Exception {
    	if(certificate == null || certificate.length()!= 18){
    		throw new Exception("身份证件号码不正确！");
    		
    	}
    	DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    	Date date = dateFormat.parse(certificate.substring(6, 14));
    	return date;
	}
}
