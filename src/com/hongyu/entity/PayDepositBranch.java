package com.hongyu.entity;

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
 * PayDeposit 收支记录 -分公司-付款-押金-门店
 */
@Entity
@Table(name = "hy_pay_deposit_branch")
public class PayDepositBranch implements java.io.Serializable {

	private Long id;
	private Integer hasPaid; // 0:未付款 1:已付款
	private String institution;
	private Date applyDate;
	private String appliName;
	private BigDecimal amount;
	private BigDecimal balance;
	private String remark;
	private Long bankListId; // 门店的银行帐号

	private String payer;
	private Date payDate;
	
	private Long branchId; //分公司id  用于区分不同的分公司 

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

	@Column(name = "institution")
	public String getInstitution() {
		return this.institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	@Temporal(TemporalType.TIMESTAMP)
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
	
	
	@Column(name = "branch_id")
	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}
}