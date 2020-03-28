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
 * PayServicer
 * @author xyy
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_pay_servicer")
public class PayServicer implements java.io.Serializable {

	private Long id;
	/** 申请人的部门id*/
	private Long departmentId;
	/** 审核表id*/
	private Long reviewId;
	private Integer hasPaid;
	/** 1:分公司预付款 2:T+N 3:提前打款 4:旅游元素供应商尾款 5:向酒店/门票/酒加景供应商付款 6:江泰预充值 7:总公司预付款 8:门店提现 9:分公司提现*/
	private Integer type;
	private Date applyDate;
	private String appliName;
	private Long servicerId;
	private String servicerName;
	private String confirmCode;
	private BigDecimal amount;
	private String remark;
	private Long bankListId;

	/** 账户名称*/
	private String accountName;
	/** 开户行*/
	private String bankName;
	/** 银行联行号*/
	private String bankCode;
	/** 账户类型  0:对私  1:对公*/
	private Integer bankType;
	/** 账号*/
	private String bankAccount;
	
	private String payer;
	private Date payDate;

	@Column(name = "payer")
	public String getPayer() {
		return payer;
	}

	public void setPayer(String payer) {
		this.payer = payer;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pay_date" )
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

	@Column(name = "department_id")
	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	@Column(name = "review_id")
	public Long getReviewId() {
		return this.reviewId;
	}

	public void setReviewId(Long reviewId) {
		this.reviewId = reviewId;
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

	@Column(name = "servicer_id")
	public Long getServicerId() {
		return this.servicerId;
	}

	public void setServicerId(Long servicerId) {
		this.servicerId = servicerId;
	}

	@Column(name = "servicer_name")
	public String getServicerName() {
		return this.servicerName;
	}

	public void setServicerName(String servicerName) {
		this.servicerName = servicerName;
	}

	@Column(name = "confirm_code")
	public String getConfirmCode() {
		return this.confirmCode;
	}

	public void setConfirmCode(String confirmCode) {
		this.confirmCode = confirmCode;
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

	@Column(name = "bank_list_id")
	public Long getBankListId() {
		return this.bankListId;
	}

	public void setBankListId(Long bankListId) {
		this.bankListId = bankListId;
	}

	@Column(name = "account_name")
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@Column(name = "bank_name")
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	@Column(name = "bank_code")
	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	@Column(name = "bank_type")
	public Integer getBankType() {
		return bankType;
	}

	public void setBankType(Integer bankType) {
		this.bankType = bankType;
	}

	@Column(name = "bank_account")
	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
}
