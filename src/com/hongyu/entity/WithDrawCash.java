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

@Entity
@Table(name = "hy_withdraw_cash")
public class WithDrawCash implements Serializable{
	private Long id;
	//门店id
	private Long storeId;
	//门店名
	private String storeName;
	private Integer cash;
	/*
	0未审核
	1已审核，待支付
	2已付款
	3已驳回
	*/
	private Integer status;
	private Date applyTime;
	private Date financeAuditTime;
	private String auditor;
	private Date payTime;
	private String payer;
	private String rejectRemark;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", unique=true, nullable=false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "store_id")
	public Long getStoreId() {
		return storeId;
	}
	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}
	@Column(name = "store_name")
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
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
	
	
	
	
}
