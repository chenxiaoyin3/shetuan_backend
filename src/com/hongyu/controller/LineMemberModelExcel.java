package com.hongyu.controller;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


/**
 * 票务产品订购批量导入游客信息模板
 * author:GSbing
 * date:20190716
*/

public class LineMemberModelExcel {
	//线路游客批量导入用类
    public static class LineMember{
    	private String name; //姓名
		private Integer certificateType;	//证件类型
    	private String certificate; //证件号码
		private Date birthday;//出生年月
		private Integer gender;//性别 0女1男
		private Integer age;//年龄
		private Integer type;//游客类型 0(成人)/1（儿童）/2（学生）/3（老人）
		private Integer priceType;//价格类型 0(成人)/1（儿童）/2（学生）/3（老人）
    	private String telephone; //手机号

		public LineMember(String name, Integer certificateType, String certificate, Date birthday, Integer gender, Integer age, Integer type, Integer priceType, String telephone) {
			this.name = name;
			this.certificateType = certificateType;
			this.certificate = certificate;
			this.birthday = birthday;
			this.gender = gender;
			this.age = age;
			this.type = type;
			this.priceType = priceType;
			this.telephone = telephone;
		}

		public Integer getCertificateType() {
			return certificateType;
		}

		public void setCertificateType(Integer certificateType) {
			this.certificateType = certificateType;
		}

		public String getCertificate() {
			return certificate;
		}

		public void setCertificate(String certificate) {
			this.certificate = certificate;
		}

		public Date getBirthday() {
			return birthday;
		}

		public void setBirthday(Date birthday) {
			this.birthday = birthday;
		}

		public Integer getGender() {
			return gender;
		}

		public void setGender(Integer gender) {
			this.gender = gender;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}

		public Integer getType() {
			return type;
		}

		public void setType(Integer type) {
			this.type = type;
		}

		public Integer getPriceType() {
			return priceType;
		}

		public void setPriceType(Integer priceType) {
			this.priceType = priceType;
		}

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getTelephone() {
			return telephone;
		}
		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}
    }
    

    //线路批量导入用读取excel文件
    public static ArrayList<LineMember> readMemberExcel(InputStream inputStream) {
 
		ArrayList<LineMember> members = new ArrayList<>();
		
        try {       	
            // 创建Excel工作簿文件的引用
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            // 创建对工作表的引用
           
            HSSFSheet sheet = workbook.getSheet("Sheet1");//读取第一张工作表 Sheet1
            System.out.println( sheet.getLastRowNum());
            int countRow = sheet.getLastRowNum() + 1 - 15; //模板前15行是说明
            System.out.println(countRow);
            
            
            //从第16行开始
            int startRow = 15;
            for(int i = 0; i < countRow ; i++){
                HSSFRow row = sheet.getRow(i + startRow);//Row
                
//                HSSFCell cell = row.getCell(0);//Cell
//                String number=cell.getStringCellValue();//读取单元格String内容,序号
//                System.out.println(number);
				Integer st = 1;
                String name=row.getCell(st + 1).getStringCellValue();
				if(name==null || name.equals(""))
					break;

				String certificateType = row.getCell(st + 2).getStringCellValue();
				System.out.println(certificateType);
				String certificateNumber = row.getCell(st + 3).getStringCellValue();
				System.out.println(certificateNumber);
				//年龄变为出生年月日
				Date birthday = row.getCell(st + 4).getDateCellValue();
				System.out.println(birthday);

				String sex = row.getCell(st + 5).getStringCellValue();
				System.out.println(sex);

				Integer age = (int)(row.getCell(st + 6).getNumericCellValue());
				System.out.println(age);

				String type = row.getCell(st + 7).getStringCellValue();
				System.out.println(type);

				String priceType = row.getCell(st + 8).getStringCellValue();
				System.out.println(priceType);

				String phone = row.getCell(st + 9).getStringCellValue(); //手机号

				Integer typevalue = 0;

				switch (type){
					case "成人":typevalue = 0;break;
					case "儿童":typevalue = 1;break;
					case "学生":typevalue = 2;break;
					case "老人":typevalue = 3;break;
					default:break;
				}
				Integer priceTypevalue = 0;
				switch (priceType){
					case "成人价":priceTypevalue = 0;break;
					case "儿童价":priceTypevalue = 1;break;
					case "学生价":priceTypevalue = 2;break;
					case "老人价":priceTypevalue = 3;break;
					default:break;
				}

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
                
                LineMember member = new LineMember(name,memberCT,certificateNumber,birthday,memberSex,age,typevalue,priceTypevalue,phone);
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
