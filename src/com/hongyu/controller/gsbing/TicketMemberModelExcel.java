package com.hongyu.controller.gsbing;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


/**
 * 票务产品订购批量导入游客信息模板
 * author:GSbing
 * date:20190716
*/

public class TicketMemberModelExcel {
	//门票批量导入用类
    public static class TicketMember{
    	private String name; //姓名
    	private String certificateNumber; //身份证号
    	private String telephone; //手机号
    	
    	public TicketMember(String name,String certificateNumber,String telephone) {
    		this.name=name;
    		this.certificateNumber=certificateNumber;
    		this.telephone=telephone;
    	}
    	
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getCertificateNumber() {
			return certificateNumber;
		}
		public void setCertificateNumber(String certificateNumber) {
			this.certificateNumber = certificateNumber;
		}
		public String getTelephone() {
			return telephone;
		}
		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}
    }
    
    //酒加景批量导入用类
    public static class JiujiajingMember{
    	private String name; //姓名
    	private String certificateType; //证件类型
    	private String certificate; //证件号
    	private String phone; //手机号
    	public JiujiajingMember(String name,String certificateType,String certificate,String phone) {
    		this.name=name;
    		this.certificateType=certificateType;
    		this.certificate=certificate;
    		this.phone=phone;
    	}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getCertificateType() {
			return certificateType;
		}
		public void setCertificateType(String certificateType) {
			this.certificateType = certificateType;
		}
		public String getCertificate() {
			return certificate;
		}
		public void setCertificate(String certificate) {
			this.certificate = certificate;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
    }
    
    //门票批量导入用读取excel文件
    public static ArrayList<TicketMember> readMemberExcel(InputStream inputStream) {
 
		ArrayList<TicketMember> members = new ArrayList<>();
		
        try {       	
            // 创建Excel工作簿文件的引用
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            // 创建对工作表的引用
           
            HSSFSheet sheet = workbook.getSheet("Sheet1");//读取第一张工作表 Sheet1
            System.out.println( sheet.getLastRowNum());
            int countRow = sheet.getLastRowNum() + 1 - 15; //模板前15行是说明
            System.out.println(countRow);
            
            
            //从第四行开始
            int startRow = 15;
            for(int i = 0; i < countRow ; i++){
                HSSFRow row = sheet.getRow(i + startRow);//Row
                
//                HSSFCell cell = row.getCell(0);//Cell
//                String number=cell.getStringCellValue();//读取单元格String内容,序号
//                System.out.println(number);
                String name=row.getCell(1).getStringCellValue();
                String certificateNumber = row.getCell(2).getStringCellValue();
                System.out.println(certificateNumber);
                String telephone = row.getCell(3).getStringCellValue();
                System.out.println(telephone);
                if(name==null || name.equals(""))
                	break;
                
                TicketMember member = new TicketMember(name, certificateNumber, telephone);
                members.add(member);
            }
            inputStream.close();
        }//第二个排序 
        catch (Exception e) {
            System.out.println("已运行xlRead() : " + e);
        }
        return members;
    }
    
    //酒加景批量导入游客用读取excel文件
    public static ArrayList<JiujiajingMember> readJiujiajingMemberExcel(InputStream inputStream) {
 
		ArrayList<JiujiajingMember> members = new ArrayList<>();
		
        try {
        	System.out.println(inputStream.available());
        	
            // 创建Excel工作簿文件的引用
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            // 创建对工作表的引用
           
            HSSFSheet sheet = workbook.getSheet("Sheet1");//读取第一张工作表 Sheet1
            System.out.println( sheet.getLastRowNum());
            int countRow = sheet.getLastRowNum() + 1 - 15; //模板前15行是说明
            System.out.println(countRow);
            
            
            //从第四行开始
            int startRow = 15;
            for(int i = 0; i < countRow ; i++){
                HSSFRow row = sheet.getRow(i + startRow);//Row
                
//                HSSFCell cell = row.getCell(0);//Cell
//                String number=cell.getStringCellValue();//读取单元格String内容,序号
//                System.out.println(number);
                String name=row.getCell(2).getStringCellValue(); //姓名
                String certificateType = row.getCell(3).getStringCellValue(); //证件类型
                String certificate=row.getCell(4).getStringCellValue(); //证件号
                String phone = row.getCell(5).getStringCellValue(); //手机号
                if(name==null || name.equals(""))
                	break;
                
                JiujiajingMember member = new JiujiajingMember(name, certificateType,certificate, phone);
                members.add(member);
            }
            inputStream.close();
        }//第二个排序 
        catch (Exception e) {
            System.out.println("已运行xlRead() : " + e);
        }
        return members;
    }
}
