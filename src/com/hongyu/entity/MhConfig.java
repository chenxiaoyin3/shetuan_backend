package com.hongyu.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 门户配置
 * @author LBC
 * @version 2019年5月29日 21:06:00
 */
@Entity
@Table(name = "mh_config")
public class MhConfig {
	/*主键id*/
	private Long id;
	/*更新时间*/
	private Date updateTime;
	/*电话*/
	private String phone;
	/*公司说明（多行）*/
	private String information;
	/*公司号*/
	private String companyCode;
	/*版权所有*/
	private String copyright;
	/*公司简介*/
	private String introduction;
	/*联系我们*/
	private String contactUs;
	/*诚聘英才*/
	private String jobWanted;
	/*隐私保护*/
	private String privacyProtection;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id",unique = true,nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time",length=19)
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	@Column(name = "phone")
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	@Column(name = "information")
	public String getInformation() {
		return information;
	}
	public void setInformation(String information) {
		this.information = information;
	}
	@Column(name = "company_code")
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	@Column(name = "copyright")
	public String getCopyright() {
		return copyright;
	}
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	@Column(name = "introduction")
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	@Column(name = "contact_us")
	public String getContactUs() {
		return contactUs;
	}
	public void setContactUs(String contactUs) {
		this.contactUs = contactUs;
	}
	@Column(name = "job_wanted")
	public String getJobWanted() {
		return jobWanted;
	}
	public void setJobWanted(String jobWanted) {
		this.jobWanted = jobWanted;
	}
	@Column(name = "privacy_protection")
	public String getPrivacyProtection() {
		return privacyProtection;
	}
	public void setPrivacyProtection(String privacyProtection) {
		this.privacyProtection = privacyProtection;
	}
	
	
	
	
}
