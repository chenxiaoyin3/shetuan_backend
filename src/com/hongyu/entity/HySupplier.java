package com.hongyu.entity;
// default package
// Generated 2018-1-12 15:36:02 by Hibernate Tools 3.6.0.Final

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.grain.entity.BaseEntity;
import com.hongyu.util.Constants.AuditStatus;

/**
 * HySupplier generated by hbm2java
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
})
@Entity
@Table(name = "hy_supplier", uniqueConstraints = @UniqueConstraint(columnNames = "supplier_name"))
public class HySupplier extends BaseEntity {

	private String supplierName;
	private Boolean isLine;
	private String yycode;
	private String yy;
	private String jycode;
	private String jy;
	private HyArea area;
	private String address;
	private String intro;
	private Boolean isInner;
	private Boolean isDijie;
	private Boolean isCaigouqian;
	private Boolean isActive;
	private AuditStatus supplierStatus;
	private HyAdmin operator;
	
	/** 是否vip */
	private Boolean isVip;
	
	/** 品牌名称 */
	private String pinpaiName;
	
	/*用于票务部供应商*/
	private String adminName; //负责人姓名
	private String adminPhone;
	private Boolean isCancel; //true-取消，false-正常
	private BankList bankId;
	

	private Set<HySupplierContract> hySupplierContracts = new HashSet<HySupplierContract>(0);

	@JsonProperty
	@Column(name = "supplier_name", unique = true, length = 11, nullable = false)
	public String getSupplierName() {
		return this.supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	@JsonProperty
	@Column(name = "is_line")
	public Boolean getIsLine() {
		return this.isLine;
	}

	public void setIsLine(Boolean isLine) {
		this.isLine = isLine;
	}

	@JsonProperty
	@Column(name = "yycode")
	public String getYycode() {
		return this.yycode;
	}

	public void setYycode(String yycode) {
		this.yycode = yycode;
	}

	@JsonProperty
	@Column(name = "yy")
	public String getYy() {
		return this.yy;
	}

	public void setYy(String yy) {
		this.yy = yy;
	}

	@JsonProperty
	@Column(name = "jycode")
	public String getJycode() {
		return this.jycode;
	}

	public void setJycode(String jycode) {
		this.jycode = jycode;
	}

	@JsonProperty
	@Column(name = "jy")
	public String getJy() {
		return this.jy;
	}

	public void setJy(String jy) {
		this.jy = jy;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "area")
	public HyArea getArea() {
		return this.area;
	}

	public void setArea(HyArea area) {
		this.area = area;
	}

	@JsonProperty
	@Column(name = "address")
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@JsonProperty
	@Column(name = "intro")
	public String getIntro() {
		return this.intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	@JsonProperty
	@Column(name = "is_inner")
	public Boolean getIsInner() {
		return this.isInner;
	}

	public void setIsInner(Boolean isInner) {
		this.isInner = isInner;
	}

	@JsonProperty
	@Column(name = "is_dijie")
	public Boolean getIsDijie() {
		return this.isDijie;
	}

	public void setIsDijie(Boolean isDijie) {
		this.isDijie = isDijie;
	}

	@JsonProperty
	@Column(name = "is_caigouqian")
	public Boolean getIsCaigouqian() {
		return this.isCaigouqian;
	}

	public void setIsCaigouqian(Boolean isCaigouqian) {
		this.isCaigouqian = isCaigouqian;
	}

	@JsonProperty
	@Column(name = "is_active")
	public Boolean getIsActive() {
		return this.isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	@JsonProperty
	@Column(name = "supplier_status")
	public AuditStatus getSupplierStatus() {
		return this.supplierStatus;
	}

	public void setSupplierStatus(AuditStatus supplierStatus) {
		this.supplierStatus = supplierStatus;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operator")
	public HyAdmin getOperator() {
		return this.operator;
	}

	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}
	
	@OrderBy("id asc")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hySupplier")
	public Set<HySupplierContract> getHySupplierContracts() {
		return this.hySupplierContracts;
	}

	public void setHySupplierContracts(Set<HySupplierContract> hySupplierContracts) {
		this.hySupplierContracts = hySupplierContracts;
	}

	@JsonProperty
	public Boolean getIsVip() {
		return isVip;
	}

	public void setIsVip(Boolean isVip) {
		this.isVip = isVip;
	}

	@JsonProperty
	public String getPinpaiName() {
		return pinpaiName;
	}

	public void setPinpaiName(String pinpaiName) {
		this.pinpaiName = pinpaiName;
	}

	@JsonProperty
	@Column(name = "admin_name")
	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	@JsonProperty
	@Column(name = "admin_phone")
	public String getAdminPhone() {
		return adminPhone;
	}

	public void setAdminPhone(String adminPhone) {
		this.adminPhone = adminPhone;
	}

	@JsonProperty
	@Column(name = "is_cancel")
	public Boolean getIsCancel() {
		return isCancel;
	}

	public void setIsCancel(Boolean isCancel) {
		this.isCancel = isCancel;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bank_id")
	public BankList getBankId() {
		return bankId;
	}

	public void setBankId(BankList bankId) {
		this.bankId = bankId;
	}
	
	@PrePersist
	public void prePersist() {
		this.isActive = false;
		this.supplierStatus = AuditStatus.unsubmitted;
	}
}
