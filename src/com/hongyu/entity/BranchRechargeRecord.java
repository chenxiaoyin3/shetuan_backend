package com.hongyu.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "hy_branch_recharge_record")
public class BranchRechargeRecord {
	public Long id;
	public boolean hasPaid;  //是否付款
	public Date payDate;  //付款日期
 	public String appliName;   //申请人
	public BigDecimal amount;  //金额
	public String remark;    //备注
	public Long branchId;    //分公司id
	public Long branchRechargeId;   //充值申请表id

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "has_paid")
	public boolean isHasPaid() {
		return hasPaid;
	}

	public void setHasPaid(boolean hasPaid) {
		this.hasPaid = hasPaid;
	}

	@Column(name = "applidate")
	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	@Column(name = "appliname")
	public String getAppliName() {
		return appliName;
	}

	public void setAppliName(String appliName) {
		this.appliName = appliName;
	}

	@Column(name = "amount")
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name = "remark")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "branch_id")
	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	@Column(name = "branch_recharge_id")
	public Long getBranchRechargeId() {
		return branchRechargeId;
	}

	public void setBranchRechargeId(Long branchRechargeId) {
		this.branchRechargeId = branchRechargeId;
	}

}
