package com.hongyu.controller.lbc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.web.bind.annotation.RequestMapping;

public class TestExcel {
	//@RequestMapping(value = "order_pay/customer_information")
	//@ResponseBody
	public void GetExcel() {
		
        try {
        	File file = new File("WebRoot/download/投保人员信息表.xls");

            File directory = new File("");//设定为当前文件夹 
            System.out.println(directory.getCanonicalPath());//获取标准的路径 
            System.out.println(directory.getAbsolutePath());//获取绝对路径
            if (!file.exists()) {
                System.out.println("文件不存在");
                return;
                
            } else {

                // 设置相应头，控制浏览器下载该文件，这里就是会出现当你点击下载后，出现的下载地址框
                String filefullname = "webRoot/download/投保人员信息表.xls";
        		 

        		//String zipfilefullname = userdir + zipFileName;
        		FileInputStream fis = new FileInputStream(new File(filefullname));
        		BufferedInputStream bis = new BufferedInputStream(fis);


        		byte[] bytes = new byte[1024];
        		int i = 0;

        		bis.close();

            }
        }
        catch (Exception e) {
			// TODO: handle exception
            System.out.println("出现错误");
		}
        return;
    


			
			
//			Map<String, Object> map = new HashMap<String, Object>(); 
//			map.put("Insurance", insurance);
		
	}
	
	
	public static class B{
		Integer aInteger;

		public Integer getaInteger() {
			return aInteger;
		}

		public void setaInteger(Integer aInteger) {
			this.aInteger = aInteger;
		}
		
		
	}
	
	public static void main(String[] args) {
//		TestExcel testExcel = new TestExcel();
//		System.out.println(System.getProperty("hongyu.webapp") + "WebRoot/download/投保人员信息表.xls");
//		testExcel.GetExcel();
		B b = new B();
		b.setaInteger(0);
		
		Integer a = b.getaInteger();
		a = 1;
		System.out.println(a);
		System.out.println(b.getaInteger());
	}
}
