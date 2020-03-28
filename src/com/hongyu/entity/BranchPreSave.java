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
 * BranchPreSave 财务中心 - 分公司预存款
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_branch_pre_save")
public class BranchPreSave implements java.io.Serializable {

	private Long id;
	/**hy_department表的id而不是hy_company表的id*/
	private Long branchId;
	private String branchName;
	private String departmentName;
    /**1余额充值  2预付使用 3分公司产品中心结算  4分公司供应商付尾款,5供应商线路返利
    6订单抵扣,7网银充值未成功,8网银充值成功 9分公司提现 10退团 11售后退款
    12酒店退款13门票退款14酒加景退款15签证退款16认购门票退款,17派遣导游退款,18供应商驳回*/
	private Integer type;
	private Date date;
	private BigDecimal amount;
	private BigDecimal preSaveBalance;
	private String remark;
	private Long orderId;
	// 网银充值订单编号
	private String bankChargeOrderSn;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "branch_id")
	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	@Column(name = "branch_name")
	public String getBranchName() {
		return this.branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	@Column(name = "department_name")
	public String getDepartmentName() {
		return this.departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	@Column(name = "type")
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

	@Column(name = "amount")
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name = "pre_save_balance")
	public BigDecimal getPreSaveBalance() {
		return this.preSaveBalance;
	}

	public void setPreSaveBalance(BigDecimal preSaveBalance) {
		this.preSaveBalance = preSaveBalance;
	}

	@Column(name = "remark")
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	@Column(name = "order_id")
	public Long getOrderId() {
		return this.orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	@Column(name = "bank_charge_order_sn")
	public String getBankChargeOrderSn() {
		return bankChargeOrderSn;
	}

	public void setBankChargeOrderSn(String bankChargeOrderSn) {
		this.bankChargeOrderSn = bankChargeOrderSn;
	}	
}
