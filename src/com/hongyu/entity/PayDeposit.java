package com.hongyu.entity;
// Generated 2017-12-22 15:00:37 by Hibernate Tools 5.2.3.Final

import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * PayDeposit  收支记录 -总公司-付款-押金(门店、供应商)
 * @author xyy
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_pay_deposit")
public class PayDeposit implements java.io.Serializable {

	private Long id;
	/** 1:门店保证金退还 2:供应商保证金退还*/
	private Integer depositType;
	/** 0:未付款 1:已付款*/
	private Integer hasPaid;
	private String institution;
	private Date applyDate;
	private String appliName;
	private String contractCode;
	private BigDecimal amount;
	private BigDecimal balance;
	private String remark;
	/** 门店或供应商的银行帐号*/
	private Long bankListId;
	private String payer;
	private Date payDate;

	@Column(name = "payer")
	public String getPayer() {
		return payer;
	}

	public void setPayer(String payer) {
		this.payer = payer;
	}

	@Temporal(TemporalType.DATE)
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

	@Column(name = "deposit_type")
	public Integer getDepositType() {
		return this.depositType;
	}

	public void setDepositType(Integer depositType) {
		this.depositType = depositType;
	}

	@Column(name = "has_paid")
	public Integer getHasPaid() {
		return this.hasPaid;
	}

	public void setHasPaid(Integer hasPaid) {
		this.hasPaid = hasPaid;
	}

	@Column(name = "institution")
	public String getInstitution() {
		return this.institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "apply_date")
	public Date getApplyDate() {
		return this.applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	@Column(name = "appli_name")
	public String getAppliName() {
		return this.appliName;
	}

	public void setAppliName(String appliName) {
		this.appliName = appliName;
	}

	@Column(name = "contract_code")
	public String getContractCode() {
		return this.contractCode;
	}

	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}

	@Column(name = "amount")
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	@Column(name = "balance")
	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	@Column(name = "remark")
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "bank_list_id")
	public Long getBankListId() {
		return bankListId;
	}

	public void setBankListId(Long bankListId) {
		this.bankListId = bankListId;
	}
}
