package com.hongyu.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * PayGuider 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_pay_guider")
public class PayGuider implements java.io.Serializable {

	private Long id;
	private Integer hasPaid;
	/** 1:导游报账应付款  2:导游费用*/
	private Integer type;
	private Long guiderId;
	private String guider;
	private BigDecimal amount;
	private String remark;

	private String bankName;  // 银行名称
	private String accountName; // 帐号名称
	private String bankAccount; // 银行账号
	private String bankLink; // 银行联行号

	private String payer;
	private Date payDate;
	private Long settlementId;  //hy_Guide_settlement_id

	@Column(name = "payer")
	public String getPayer() {
		return payer;
	}

	public void setPayer(String payer) {
		this.payer = payer;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pay_date")
	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "has_paid")
	public Integer getHasPaid() {
		return this.hasPaid;
	}

	public void setHasPaid(Integer hasPaid) {
		this.hasPaid = hasPaid;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "guider_id")
	public Long getGuiderId() {
		return this.guiderId;
	}

	public void setGuiderId(Long guiderId) {
		this.guiderId = guiderId;
	}

	@Column(name = "guider")
	public String getGuider() {
		return this.guider;
	}

	public void setGuider(String guider) {
		this.guider = guider;
	}

	@Column(name = "amount")
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name = "remark")
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "bank_name")
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	@Column(name = "account_name")
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@Column(name = "bank_account")
	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	@Column(name = "bank_link")
	public String getBankLink() {
		return bankLink;
	}

	public void setBankLink(String bankLink) {
		this.bankLink = bankLink;
	}

	@Column(name = "settlement_id")
	public Long getSettlementId() {
		return settlementId;
	}

	public void setSettlementId(Long settlementId) {
		this.settlementId = settlementId;
	}
	
	

}
