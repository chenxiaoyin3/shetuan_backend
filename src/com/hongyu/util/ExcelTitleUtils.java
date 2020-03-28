package com.hongyu.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExcelTitleUtils {
	public static String doOrderTurnover(Date startTime,Date endTime,Integer condition,String sub){
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		StringBuffer sb2 = new StringBuffer();
		if(startTime!=null){
			sb2.append(format.format(startTime));
		}
		sb2.append(" 至 ");
		if(endTime!=null){
			sb2.append(format.format(endTime));
		}else{
			sb2.append(format.format(new Date()));
		}
		sb2.append(" ");
		if(condition==null || condition==0){
			condition=3;
		}
		switch (condition) {
		case 1:
			sb2.append("按订单量");
			break;
		case 2:
			sb2.append("按收客人数");
			break;
		default:
			sb2.append("按总计金额");
			break;
		}
		sb2.append(sub);
		return sb2.toString();
	}

}
