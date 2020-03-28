package com.hongyu.util;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/** DES加密解密*/
public class DESUtils {
	private static Key key;
	private static final String KEY_STR = "asdfASDF1234!@#$";
	
	static {
		try {
			KeyGenerator generator = KeyGenerator.getInstance("DES");
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(KEY_STR.getBytes()); 
			generator.init(secureRandom);
			key = generator.generateKey();
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** DES加密*/
	public static String getEncryptString(String str){  
        BASE64Encoder base64Encoder = new BASE64Encoder();  
        System.out.println(key);  
        try  
        {  
            byte[] strBytes = str.getBytes("UTF-8");  
            Cipher cipher = Cipher.getInstance("DES");  
            cipher.init(Cipher.ENCRYPT_MODE, key);  
            byte[] encryptStrBytes = cipher.doFinal(strBytes);  
            return base64Encoder.encode(encryptStrBytes);  
        }  
        catch (Exception e)  
        {  
            throw new RuntimeException(e);  
        }  
          
    }  

    /** DES解密*/
	public static String getDecryptString(String str){  
        BASE64Decoder base64Decoder = new BASE64Decoder();  
        try  
        {  
            byte[] strBytes = base64Decoder.decodeBuffer(str);  
            Cipher cipher = Cipher.getInstance("DES");  
            cipher.init(Cipher.DECRYPT_MODE, key);  
            byte[] encryptStrBytes = cipher.doFinal(strBytes);  
            return new String(encryptStrBytes,"UTF-8");  
        }  
        catch (Exception e)  
        {  
            throw new RuntimeException(e);  
        }  
          
    }  

}
