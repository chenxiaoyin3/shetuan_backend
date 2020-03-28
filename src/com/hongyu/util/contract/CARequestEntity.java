package com.hongyu.util.contract;

public class CARequestEntity {
	//客户姓名
	private String name;	
	//邮件地址
	private String email = "";
	//证件号码
	private String idCard;
	//证件类型  默认为0 表示身份证
	private String idenType = "0";
	//电话号码
	private String phone;
	
	public CARequestEntity() {
		super();
	}
	/**
	 * 
	 * @param name	客户姓名
	 * @param email	邮件地址
	 * @param idCard	证件号码
	 * @param idenType	证件类型
	 * @param phone	电话号码
	 */
	public CARequestEntity(String name, String email, String idCard, String idenType, String phone) {
		super();
		this.name = name;
		this.email = email;
		this.idCard = idCard;
		this.idenType = idenType;
		this.phone = phone;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	public String getIdenType() {
		return idenType;
	}
	public void setIdenType(String idenType) {
		this.idenType = idenType;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}
