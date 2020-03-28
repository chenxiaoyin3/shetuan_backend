package com.hongyu.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * BranchPrePayDetail 
 * 财务中心 - 分公司预付款 - 分公司预付款使用记录详情
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_branch_pre_pay_detail")
public class BranchPrePayDetail implements java.io.Serializable {

	private Long id;
	private Long branchPrePayId;
	private Integer type; // 1 预付给供应商(充值)  2使用预付款(冲抵)  
	private Date date;
	private BigDecimal amount;
	private String appliname;
	private String remark;
	private BigDecimal prePayBalance;
//	private Long payServicerId;
	private Long groupId;


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "branch_pre_pay_id", nullable = false)
	public Long getBranchPrePayId() {
		return this.branchPrePayId;
	}

	public void setBranchPrePayId(Long branchPrePayId) {
		this.branchPrePayId = branchPrePayId;
	}

	@Column(name = "type", nullable = false)
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date")
	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}


	@Column(name = "amount", nullable = false, precision = 21, scale = 3)
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name = "appliname", nullable = false)
	public String getAppliname() {
		return this.appliname;
	}

	public void setAppliname(String appliname) {
		this.appliname = appliname;
	}

	@Column(name = "remark")
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "pre_pay_balance", nullable = false, precision = 21, scale = 3)
	public BigDecimal getPrePayBalance() {
		return this.prePayBalance;
	}

	public void setPrePayBalance(BigDecimal prePayBalance) {
		this.prePayBalance = prePayBalance;
	}

//	@Column(name = "pay_servicer_id")
//	public Long getPayServicerId() {
//		return payServicerId;
//	}
//
//	public void setPayServicerId(Long payServicerId) {
//		this.payServicerId = payServicerId;
//	}

	@Column(name = "group_id")
	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	
	
	

}
