package com.hongyu.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;


public class SubmitConfirm implements java.io.Serializable{
	private String payCode;
	private String supplierName;
	private String alias;
	private String bankName;
	private String accountName;
	private String bankAccount;
	private String bankCode;
	private Boolean bankType;
	private String contractCode;
	private String name;
	private BigDecimal lineOrderSum;
	public String getPayCode() {
		return payCode;
	}
	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getBankAccount() {
		return bankAccount;
	}
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public Boolean getBankType() {
		return bankType;
	}
	public void setBankType(Boolean bankType) {
		this.bankType = bankType;
	}
	public String getContractCode() {
		return contractCode;
	}
	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BigDecimal getLineOrderSum() {
		return lineOrderSum;
	}
	public void setLineOrderSum(BigDecimal lineOrderSum) {
		this.lineOrderSum = lineOrderSum;
	}

}
