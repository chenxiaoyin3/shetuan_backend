package com.hongyu.controller.lbc;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;




public class MoBanExcelUtil {
	//public static String fileToBeRead = "WebRoot/download/投保人员信息表test.xls";//要打開的Excel的位置
	public static class Member{
		String name;
		//0：身份证          1：护照        2：港澳台通行证 3：士兵证       4：回乡证  5：出生年月日
		Integer certificateType;
		String certificateNumber;
		Integer age;
		//String phone;
		//0女 1男
		Integer sex;
		
		public Member(String name, Integer certificateType, String certificateNumber, Integer age,
				Integer sex) {
			this.name = name;
			this.certificateType = certificateType;
			this.certificateNumber = certificateNumber;
			this.age = age;
			//this.phone = phone;
			this.sex = sex;
		}
		
		public String toString() {
			return "name: " + name + "\n" + "certificateType: " + certificateType + "\n"
					+ "certificateNumber: " + certificateNumber + "\n" + "age: " + age + "\n"
					+ "\n";
		}

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

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}

//		public String getPhone() {
//			return phone;
//		}
//
//		public void setPhone(String phone) {
//			this.phone = phone;
//		}

		public Integer getSex() {
			return sex;
		}

		public void setSex(Integer sex) {
			this.sex = sex;
		}
		
		
		
	}
	
	public static ArrayList<Member> readExcel(InputStream inputStream) {
        //對Excel的讀取
		ArrayList<Member> members = new ArrayList<>();
		
        try {
        	System.out.println(inputStream.available());
        	
            // 創建對Excel工作簿文件的引用
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            // 創建對工作表的引用
           
            HSSFSheet sheet = workbook.getSheet("Sheet1");//讀取第一張工作表 Sheet1
            System.out.println( sheet.getLastRowNum());
            int countRow = sheet.getLastRowNum() + 1 - 3;
            System.out.println(countRow);
            //Member members[] = new Member[36];//共36行數據
            
            
            //从第四行开始
            int startRow = 3;
            for(int i = 0; i < countRow ; i++){
                HSSFRow row = sheet.getRow(i + startRow);//Row
                
                HSSFCell cell = row.getCell(0);//Cell
                String name=cell.getStringCellValue();//读取单元格String内容
                System.out.println(name);
                String certificateType = row.getCell(1).getStringCellValue();
                System.out.println(certificateType);
                String certificateNumber = row.getCell(2).getStringCellValue();
                System.out.println(certificateNumber);
//                //年龄变为出生年月日
//                String birthday = row.getCell(3).getStringCellValue();
//                System.out.println(birthday);
                
                
                
                Integer age = (int) row.getCell(3).getNumericCellValue();
                System.out.println(age);
                //String phone = row.getCell(4).getStringCellValue();
                //System.out.println(phone);
                String sex = row.getCell(4).getStringCellValue();
                System.out.println(sex);
                
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
                }
                
                int memberSex = 0;
                if(sex.equals("女")) {
                	System.out.println("sex为女");
                	memberSex = 0;
                }
                else {
                	System.out.println("sex为男");
                	memberSex = 1;
                }
                
                Member member = new Member(name, memberCT, certificateNumber, age, memberSex);
                members.add(member);
            }
//            for(Member member : members) {
//            	System.out.println(member.toString());
//            }
//           
            inputStream.close();
        }//第二个排序 
        catch (Exception e) {
            System.out.println("已运行xlRead() : " + e);
        }
        return members;
    }
	
	public static ArrayList<Member> readExcelBirthday(InputStream inputStream) {
        //對Excel的讀取
		ArrayList<Member> members = new ArrayList<>();
		
        try {
        	System.out.println(inputStream.available());
        	
            // 創建對Excel工作簿文件的引用
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            // 創建對工作表的引用
           
            HSSFSheet sheet = workbook.getSheet("Sheet1");//讀取第一張工作表 Sheet1
            System.out.println( sheet.getLastRowNum());
            int countRow = sheet.getLastRowNum() + 1 - 3;
            System.out.println(countRow);
            //Member members[] = new Member[36];//共36行數據
            
            
            //从第四行开始
            int startRow = 3;
            for(int i = 0; i < countRow ; i++){
                HSSFRow row = sheet.getRow(i + startRow);//Row
                
                HSSFCell cell = row.getCell(0);//Cell
                String name=cell.getStringCellValue();//读取单元格String内容
                System.out.println(name);
                String certificateType = row.getCell(1).getStringCellValue();
                System.out.println(certificateType);
                String certificateNumber = row.getCell(2).getStringCellValue();
                System.out.println(certificateNumber);
                //年龄变为出生年月日
                Date birthday = row.getCell(3).getDateCellValue();
                System.out.println(birthday);
                
//                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
//                Date birthdate = format.parse(birthday);
                int age = getAgeByBirth(birthday);
                
                
//                Integer age = (int) row.getCell(3).getNumericCellValue();
//                System.out.println(age);
                //String phone = row.getCell(4).getStringCellValue();
                //System.out.println(phone);
                String sex = row.getCell(4).getStringCellValue();
                System.out.println(sex);
                
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
                }
                
                int memberSex = 0;
                if(sex.equals("女")) {
                	System.out.println("sex为女");
                	memberSex = 0;
                }
                else {
                	System.out.println("sex为男");
                	memberSex = 1;
                }
                
                Member member = new Member(name, memberCT, certificateNumber, age, memberSex);
                members.add(member);
            }
//            for(Member member : members) {
//            	System.out.println(member.toString());
//            }
//           
            inputStream.close();
        }//第二个排序 
        catch (Exception e) {
            System.out.println("已运行xlRead() : " + e);
        }
        return members;
    }
	
	//由出生日期获得年龄  
    public static  int getAgeByBirth(Date birthDay) throws Exception {  
        Calendar cal = Calendar.getInstance();  
        if (cal.before(birthDay)) {  
            throw new IllegalArgumentException(  
                    "The birthDay is before Now.It's unbelievable!");  
        }  
        int yearNow = cal.get(Calendar.YEAR);  
        int monthNow = cal.get(Calendar.MONTH);  
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);  
        cal.setTime(birthDay);   
  
        int yearBirth = cal.get(Calendar.YEAR);  
        int monthBirth = cal.get(Calendar.MONTH);  
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);   
  
        int age = yearNow - yearBirth;  
  
        if (monthNow <= monthBirth) {  
            if (monthNow == monthBirth) {  
                if (dayOfMonthNow < dayOfMonthBirth) age--;  
            }else{  
                age--;  
            }  
        }  
        return age;  
    }  
	
    public static void main(String[] args) {
        //對Excel的讀取
        try {
//            // 創建對Excel工作簿文件的引用
//            HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(fileToBeRead));
//            // 創建對工作表的引用
//           
//            HSSFSheet sheet = workbook.getSheet("Sheet1");//讀取第一張工作表 Sheet1
//            System.out.println( sheet.getLastRowNum());
//            int countRow = sheet.getLastRowNum() + 1 - 3;
//            System.out.println(countRow);
//            //Member members[] = new Member[36];//共36行數據
//            ArrayList<Member> members = new ArrayList<>();
//            
//            //从第四行开始
//            int startRow = 3;
//            for(int i = 0; i < countRow ; i++){
//                HSSFRow row = sheet.getRow(i + startRow);//Row
//                
//                HSSFCell cell = row.getCell(0);//Cell
//                String name=cell.getStringCellValue();//读取单元格String内容
//                System.out.println(name);
//                String certificateType = row.getCell(1).getStringCellValue();
//                System.out.println(certificateType);
//                String certificateNumber = row.getCell(2).getStringCellValue();
//                System.out.println(certificateNumber);
//                Integer age = (int) row.getCell(3).getNumericCellValue();
//                System.out.println(age);
//                String phone = row.getCell(4).getStringCellValue();
//                System.out.println(phone);
//                String sex = row.getCell(5).getStringCellValue();
//                System.out.println(sex);
//                
//                Member member = new Member(name, certificateType, certificateNumber, age, phone, sex);
//                members.add(member);
//            }
//            for(Member member : members) {
//            	System.out.println(member.toString());
//            }
            
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            Date d1 = format.parse("1967/05/05");
            Date d2 = format.parse("1967/05/06");
            Date d3 = format.parse("1967/10/05");
            Date d4 = format.parse("1967/03/05");
            System.out.println(getAgeByBirth(d1));
            System.out.println(getAgeByBirth(d2));
            System.out.println(getAgeByBirth(d3));
            System.out.println(getAgeByBirth(d4));
        }//第二个排序 
        catch (Exception e) {
            System.out.println("已运行xlRead() : " + e);
        }   
    }
}
