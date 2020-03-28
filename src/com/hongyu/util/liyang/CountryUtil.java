package com.hongyu.util.liyang;

import com.hongyu.entity.HyVisaPic;
import com.hongyu.util.Constants.DeductPiaowu;

public class CountryUtil {
	/**
	 * 根据大洲的代码来获取实际名称
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String getContinent(Integer key) throws Exception{
		if(key==null)
			return null;
		String continent = "";
		switch (key) {
		case 0:
			continent = "南极洲";
			break;
		case 1:
			continent = "北美洲";
			break;
		case 2:
			continent = "大洋洲";
			break;
		case 3:
			continent = "非洲";
			break;
		case 4:
			continent = "南美洲";
			break;
		case 5:
			continent = "欧洲";
			break;
		case 6:
			continent = "亚洲";
			break;
		default:
			continent = "";
			break;
		}
		return continent;
	}
		
}
