package com.hongyu.util;

import java.io.UnsupportedEncodingException;

public class HexStringUtils {
	

/** 
 * 16进制String转10进制String
 *  
 * @param s 
 * @return 
 */  
public static String hexStringToString(String s) {  
    if (s == null || s.equals("")) {  
        return null;  
    }  
    s = s.replace(" ", "");  
    byte[] baKeyword = new byte[s.length() / 2];  
    for (int i = 0; i < baKeyword.length; i++) {  
        try {  
            baKeyword[i] = (byte) (0xff & Integer.parseInt(  
                    s.substring(i * 2, i * 2 + 2), 16));  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    try {  
        s = new String(baKeyword, "GBK");  
        new String();  
    } catch (Exception e1) {  
        e1.printStackTrace();  
    }  
    return s;  
}
/** 
 * 10进制String转16进制String
 *  
 * @param s 
 * @return 
 */  
public static String stringToHexString(String s ) {
	StringBuffer sb = new  StringBuffer();

	try {
		for (byte bit :s.getBytes("GBK")) {
			char hex = (char) (bit&0xFF);
			sb.append(Integer.toHexString(hex));
			
		}
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	}
	
	return sb.toString();
}

}
