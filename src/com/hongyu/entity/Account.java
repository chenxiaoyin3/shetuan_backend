package com.hongyu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "hy_account")
public class Account implements java.io.Serializable {
	private Long id;
	private Integer institutionType; //1:总公司  2:分公司  
	private Long institutionId;
	private Integer concreteType; //1:银行帐号 2:支付宝 3:微信支付 
	private String accountName;
	private String bankName;
	private String bankCode;
	private Integer bankType; // 0 对私 1 对公
	private String bankAccount;
	private String alias;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "institution_type")
	public Integer getInstitutionType() {
		return institutionType;
	}

	public void setInstitutionType(Integer institutionType) {
		this.institutionType = institutionType;
	}

	@Column(name = "institution_id")
	public Long getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(Long institutionId) {
		this.institutionId = institutionId;
	}

	@Column(name = "concrete_type")
	public Integer getConcreteType() {
		return concreteType;
	}

	public void setConcreteType(Integer concreteType) {
		this.concreteType = concreteType;
	}

	@Column(name = "account_name")
	public String getAccountName() {
		return this.accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@Column(name = "bank_name")
	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	@Column(name = "bank_code")
	public String getBankCode() {
		return this.bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	@Column(name = "bank_type")
	public Integer getBankType() {
		return this.bankType;
	}

	public void setBankType(Integer bankType) {
		this.bankType = bankType;
	}

	@Column(name = "bank_account", nullable = false)
	public String getBankAccount() {
		return this.bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	@Column(name = "alias")
	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

}
