package com.hongyu.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("serial")
@Entity
@Table(name = "hy_withdraw_cash_subCompany")
public class WithDrawCashSubCompany implements Serializable{
	private Long id;
	//门店id
	private Long departmentId;
	//门店名
	private String departmentName;
	private Integer cash;
	private Integer status;
	private Date applyTime;
	private Date financeAuditTime;
	private String auditor;
	private Date payTime;
	private String payer;
	private String rejectRemark;
	
	private Long bankListId;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", unique=true, nullable=false)
	public Long getId() {
		return id;
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
	@Column(name = "department_name")
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	@Column(name = "cash")
	public Integer getCash() {
		return cash;
	}
	public void setCash(Integer cash) {
		this.cash = cash;
	}
	
	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "apply_time")
	public Date getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "finance_audit_time")
	public Date getFinanceAuditTime() {
		return financeAuditTime;
	}
	public void setFinanceAuditTime(Date financeAuditTime) {
		this.financeAuditTime = financeAuditTime;
	}
	@Column(name = "auditor")
	public String getAuditor() {
		return auditor;
	}
	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pay_time")
	public Date getPayTime() {
		return payTime;
	}
	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}
	@Column(name = "payer")
	public String getPayer() {
		return payer;
	}
	public void setPayer(String payer) {
		this.payer = payer;
	}
	@Column(name = "reject_remark")
	public String getRejectRemark() {
		return rejectRemark;
	}
	public void setRejectRemark(String rejectRemark) {
		this.rejectRemark = rejectRemark;
	}
	
	@Column(name = "banklist_id")
	public Long getBankListId() {
		return bankListId;
	}
	public void setBankListId(Long bankListId) {
		this.bankListId = bankListId;
	}
	
	
	
	
}

