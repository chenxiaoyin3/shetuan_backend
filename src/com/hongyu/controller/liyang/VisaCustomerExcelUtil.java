package com.hongyu.controller.liyang;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.hongyu.util.DateUtil;

public class VisaCustomerExcelUtil {
	//public static String fileToBeRead = "WebRoot/download/投保人员信息表test.xls";//要打開的Excel的位置
		public static class VisaMember{
			String name;
			//0：身份证          1：护照        2：港澳台通行证 3：士兵证       4：回乡证  5：出生年月日
			Integer certificateType;
			String certificateNumber;
			Date birthday;
			//0女 1男
			Integer sex;
			String xing;
			String ming;
			Date youxiaoqi;
			String jiguan;
			String phone;
			public String getName() {
				return name;
			}
			public void setName(String name) {
				this.name = name;
			}
			public Integer getCertificateType() {
				return certificateType;
			}
			public void setCertificateType(Integer certificateType) {
				this.certificateType = certificateType;
			}
			public String getCertificateNumber() {
				return certificateNumber;
			}
			public void setCertificateNumber(String certificateNumber) {
				this.certificateNumber = certificateNumber;
			}
			public Date getBirthday() {
				return birthday;
			}
			public void setBirthday(Date birthday) {
				this.birthday = birthday;
			}
			public Integer getSex() {
				return sex;
			}
			public void setSex(Integer sex) {
				this.sex = sex;
			}
			public String getXing() {
				return xing;
			}
			public void setXing(String xing) {
				this.xing = xing;
			}
			public String getMing() {
				return ming;
			}
			public void setMing(String ming) {
				this.ming = ming;
			}
			public Date getYouxiaoqi() {
				return youxiaoqi;
			}
			public void setYouxiaoqi(Date youxiaoqi) {
				this.youxiaoqi = youxiaoqi;
			}
			public String getJiguan() {
				return jiguan;
			}
			public void setJiguan(String jiguan) {
				this.jiguan = jiguan;
			}
			public String getPhone() {
				return phone;
			}
			public void setPhone(String phone) {
				this.phone = phone;
			}
			
			
			
		}
		
		public static ArrayList<VisaMember> readExcel(InputStream inputStream) {
	        //對Excel的讀取
			ArrayList<VisaMember> members = new ArrayList<>();
			
	        try {
	        	System.out.println(inputStream.available());
	        	
	            // 創建對Excel工作簿文件的引用
	            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
	            // 創建對工作表的引用
	           
	            HSSFSheet sheet = workbook.getSheet("Sheet1");//讀取第一張工作表 Sheet1
	            System.out.println( sheet.getLastRowNum());
	            int countRow = sheet.getLastRowNum() + 1 - 15;
	            System.out.println("countRow = "+countRow);
	                      
	            //从第16行开始
	            int startRow = 15;
	            
	            int offset = 2;
	            for(int i = 0; i < countRow ; i++){
	            	System.out.println("i = "+i);
	                HSSFRow row = sheet.getRow(i + startRow);//Row
	                
	                String name=row.getCell(0+offset).getStringCellValue();//读取单元格String内容
	                System.out.println(name);
	                String certificateType = row.getCell(1+offset).getStringCellValue();
	                System.out.println(certificateType);
	                String certificateNumber = row.getCell(2+offset).getStringCellValue();
	                System.out.println(certificateNumber);
	                String birthday =  row.getCell(3+offset).getStringCellValue();
	                System.out.println(birthday);      
	                String sex = row.getCell(4+offset).getStringCellValue();
	                System.out.println(sex);
	                String xing = row.getCell(5+offset).getStringCellValue();
	                System.out.println(xing);
	                String ming = row.getCell(6+offset).getStringCellValue();
	                System.out.println(ming);
	                String youxiaoqi = row.getCell(7+offset).getStringCellValue();
	                System.out.println(youxiaoqi);
	                String jiguan = row.getCell(8+offset).getStringCellValue();
	                System.out.println(jiguan);
	                String phone = row.getCell(9+offset).getStringCellValue();
	                System.out.println(phone);
	                Integer memberCT = 0;
	                if(certificateType.equals("身份证")) {
	                	memberCT = 0;
	                }
	                else if(certificateType.equals("护照")) {
	                	memberCT = 1;
	                }
	                else if(certificateType.equals("港澳台通行证")) {
	                	memberCT = 2;
	                }
	                else if(certificateType.equals("士兵证")) {
	                	memberCT = 3;
	                }
	                else if(certificateType.equals("回乡证")) {
	                	memberCT = 4;
	                }
	                else if(certificateType.equals("出生年月日")) {
	                	memberCT = 5;
	                }else{
	                	memberCT = 6;
	                }
	                
	                int memberSex = 0;
	                if(memberCT == 0){
	                	//从身份证中获取性别
	                	int x = certificateNumber.charAt(16) - '0';
	                	if(x%2 == 1){
	                		memberSex = 1;
	                	}
	                }else{
		                if(sex.equals("女")) {
		                	System.out.println("sex为女");
		                	memberSex = 0;
		                }
		                else {
		                	System.out.println("sex为男");
		                	memberSex = 1;
		                }
	                }
	                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	                VisaMember visaMember = new VisaMember();
	                visaMember.setName(name);
	                visaMember.setCertificateType(memberCT);
	                visaMember.setCertificateNumber(certificateNumber);
	                if(memberCT == 0){
	                	//从身份证中获取生日
	                	Date bir = DateUtil.getBirthdayByShenfenzheng(certificateNumber);
	                	visaMember.setBirthday(bir);
	                }else{
	                	visaMember.setBirthday(format.parse(birthday));
	                }
	                visaMember.setSex(memberSex);
	                visaMember.setXing(xing);
	                visaMember.setMing(ming);
	                visaMember.setYouxiaoqi(format.parse(youxiaoqi));
	                visaMember.setJiguan(jiguan);
	                visaMember.setPhone(phone);
	                members.add(visaMember);
	            }
	            inputStream.close();
	        }//第二个排序 
	        catch (Exception e) {
	            System.out.println("已运行xlRead() : " + e);
	        }
	        return members;
	    }
	    
}
