package com.hongyu.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.spi.ValidationProvider;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
@Entity
@Table(name = "hy_policyholder_info")
public class HyPolicyHolderInfo implements Serializable{
	private Long id;
	@JsonIgnore
	private InsuranceOrder insuranceOrder;//对应保单id
	private String name;//投保人姓名
	private Integer sex;//0是女  1是男
	private Integer certificate;//证件类型1身份证，2军官证，3护照，4.其他
	private String certificateNumber;//证件号
	private Date birthday;//生日
	private Integer age;//年龄
	private String downloadUrl;//个人保险凭证下载
	private Date createTime;
	@Id
	@Column(name = "id" ,nullable=false,unique=true)
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "insurance_order_id")
	public InsuranceOrder getInsuranceOrder() {
		return insuranceOrder;
	}
	public void setInsuranceOrder(InsuranceOrder insuranceOrder) {
		this.insuranceOrder = insuranceOrder;
	}
	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "sex")
	public Integer getSex() {
		return sex;
	}
	public void setSex(Integer sex) {
		this.sex = sex;
	}
	@Column(name = "certificate")
	public Integer getCertificate() {
		return certificate;
	}
	public void setCertificate(Integer certificate) {
		this.certificate = certificate;
	}
	@Column(name = "certificate_number")
	public String getCertificateNumber() {
		return certificateNumber;
	}
	public void setCertificateNumber(String certificate_number) {
		this.certificateNumber = certificate_number;
	}
	@Column(name = "birthday")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	@Column(name = "age")
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	@Column(name = "download_url")
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String download_url) {
		this.downloadUrl = download_url;
	}
	@Column(name = "create_time")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@PrePersist
	public void prePersist(){
		this.setCreateTime(new Date());
	}
	
}
