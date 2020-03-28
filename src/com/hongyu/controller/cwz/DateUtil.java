package com.hongyu.controller.cwz;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	public static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
	
    public static Date stringToDate(String data, String format) throws Exception {

        if (data == null || data.isEmpty() )
        	return null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
	 * 取得指定日期的下一天
	 * 
	 * @param dateStr
	 *            formate 年月日 yyyy-MM-dd
	 * @return
	 * @throws ParseException
	 */
	public static String getTheNextDay(String dateStr) throws ParseException {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(dateStr);
		cal.setTime(date);
		cal.add(Calendar.DATE, 1);
		Date nextDate = cal.getTime();
		String next_dateStr = new SimpleDateFormat("yyyy-MM-dd")
				.format(nextDate);
		return next_dateStr;
	}
	/**
	 * 取得指定日期的上一天
	 * 
	 * @param dateStr
	 *            formate 年月日 yyyy-MM-dd
	 * @return
	 * @throws ParseException
	 */
	public static String getThePreviousDay(String dateStr) throws ParseException {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(dateStr);
		cal.setTime(date);
		cal.add(Calendar.DATE, -1);
		Date nextDate = cal.getTime();
		String next_dateStr = new SimpleDateFormat("yyyy-MM-dd")
				.format(nextDate);
		return next_dateStr;
	}
}