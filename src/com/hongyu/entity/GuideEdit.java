package com.hongyu.entity;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="hy_guide_edit")
public class GuideEdit {

	private Long id;
	private Long guideId;
	private String phone;
	private Boolean hasCertificate;//是否有导游证 false没有true有
	private Integer rank;//导游级别 0高级，1中级，2初级
	private String touristCertificateNumber;//导游证号
	private String touristCertificate;//导游证
	private String bankName;//开户行
	private String bankAccount;//银行账号
	private String bankLink;//银行联行号
//	private Integer status;//0待审核，1通过，2驳回
	private Date createDate;//创建时间
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id",nullable=false,unique=true)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(name="guide_id")
	public Long getGuideId() {
		return guideId;
	}
	public void setGuideId(Long guideId) {
		this.guideId = guideId;
	}
	@Column(name="phone")
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	@Column(name="has_certificate")
	public Boolean getHasCertificate() {
		return hasCertificate;
	}
	public void setHasCertificate(Boolean hasCertificate) {
		this.hasCertificate = hasCertificate;
	}
	@Column(name="rank")
	public Integer getRank() {
		return rank;
	}
	public void setRank(Integer rank) {
		this.rank = rank;
	}
	@Column(name="tourist_certificate_number")
	public String getTouristCertificateNumber() {
		return touristCertificateNumber;
	}
	public void setTouristCertificateNumber(String touristCertificateNumber) {
		this.touristCertificateNumber = touristCertificateNumber;
	}
	@Column(name="tourist_certificate")
	public String getTouristCertificate() {
		return touristCertificate;
	}
	public void setTouristCertificate(String touristCertificate) {
		this.touristCertificate = touristCertificate;
	}
	@Column(name="bank_name")
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	@Column(name="bank_account")
	public String getBankAccount() {
		return bankAccount;
	}
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	@Column(name="bank_link")
	public String getBankLink() {
		return bankLink;
	}
	public void setBankLink(String bankLink) {
		this.bankLink = bankLink;
	}
//	@Column(name="status")
//	public Integer getStatus() {
//		return status;
//	}
//	public void setStatus(Integer status) {
//		this.status = status;
//	}
	@Column(name="create_date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	@PrePersist
	public void prePersist(){
		this.setCreateDate(new Date());
//		this.setStatus(0);
	}
}
