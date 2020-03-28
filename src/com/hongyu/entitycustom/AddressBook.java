package com.hongyu.entitycustom;

public class AddressBook { //员工通讯录类
	public String name;	//员工姓名
	public String departmentName; //部门
	public String phone; //电话
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

}
