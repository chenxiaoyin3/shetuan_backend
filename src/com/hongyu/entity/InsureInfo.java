package com.hongyu.entity;

public class InsureInfo implements java.io.Serializable {
private String insuredName;//被保险人姓名（被保险人列表-子集字段）
private String identifyNumber;//证件号码（被保险人列表-子集字段）
private Integer identifyType;//证件类型（被保险人列表-子集字段）
private String birthDay;//生日,yyyy-MM-dd（被保险人列表-子集字段）
private String sex;//性别（被保险人列表-子集字段）
public String getInsuredName() {
	return insuredName;
}
public void setInsuredName(String insuredName) {
	this.insuredName = insuredName;
}
public String getIdentifyNumber() {
	return identifyNumber;
}
public void setIdentifyNumber(String identifyNumber) {
	this.identifyNumber = identifyNumber;
}
public Integer getIdentifyType() {
	return identifyType;
}
public void setIdentifyType(Integer identifyType) {
	this.identifyType = identifyType;
}
public String getBirthDay() {
	return birthDay;
}
public void setBirthDay(String birthDay) {
	this.birthDay = birthDay;
}
public String getSex() {
	return sex;
}
public void setSex(String sex) {
	this.sex = sex;
}
}
