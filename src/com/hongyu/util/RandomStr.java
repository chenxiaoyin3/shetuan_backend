package com.hongyu.util;

import java.util.Random;

public class RandomStr {

	/** 随机数激活码 */
	public static String getRandomStr() {
		final int length = 16;
		StringBuilder res = new StringBuilder();
		int i = 0;
		Random random = new Random();
		while (i < length) {
			res.append(random.nextInt(10));
			i++;
		}
		return res.toString();
	}

	/** 乱码生成 */
	public static String getRandomCharacterAndNumber(int length) {
		String val = "";
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			// 输出字母还是数字
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
			// 字符串
			if ("char".equalsIgnoreCase(charOrNum))
			{
				// 取得大写字母还是小写字母
				int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
				val += (char) (choice + random.nextInt(26));
				// int choice = 97; // 指定字符串为小写字母
				val += (char) (choice + random.nextInt(26));
			} else if ("num".equalsIgnoreCase(charOrNum)) // 数字
			{
				val += String.valueOf(random.nextInt(10));
			}
		}
		return val;
	}
}
