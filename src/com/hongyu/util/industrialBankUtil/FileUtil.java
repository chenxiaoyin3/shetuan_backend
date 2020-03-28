package com.hongyu.util.industrialBankUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件工具类
 * @author GSbing
 * @version 1.0.0
 * @date 2018-09-04
 */
public class FileUtil {

    public static String getBase64FromFile(String fileName) {
        FileInputStream inputFile = null;
        try {
            File file = new File(fileName);
            inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
            return (new sun.misc.BASE64Encoder().encode(buffer)).replaceAll(System.getProperty("line.separator"), "");
        } catch (Exception e) {
            return "";
        } finally {
            if (inputFile != null)
                try {
                    inputFile.close();
                } catch (IOException e) {
                }
        }
    }

    public static void writeFileFromBase64(String base64, String fileName) throws IOException {
    	assert(base64 != null);
    	assert(fileName != null);
        FileOutputStream out = null;
        try {
            byte[] buffer = new sun.misc.BASE64Decoder().decodeBuffer(base64);
            out = new FileOutputStream(fileName);
            out.write(buffer);
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (IOException e) {
                }
        }
    }
}
