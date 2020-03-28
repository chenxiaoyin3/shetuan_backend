package com.hongyu.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * BranchPrePay 财务中心 - 预付款 - 预付款记录
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_branch_pre_pay")
public class BranchPrePay implements java.io.Serializable {

	private Long id;
	private Long branchId;
	private String branchName;
	private Long supplierElementId;
	private String servicerName;
	private Long departmentId;
	private String departmentName;
	private BigDecimal prePayBalance;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "branch_name", nullable = false)
	public String getBranchName() {
		return this.branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	
	@Column(name = "supplier_element_id")
	public Long getSupplierElementId() {
		return supplierElementId;
	}

	public void setSupplierElementId(Long supplierElementId) {
		this.supplierElementId = supplierElementId;
	}

	@Column(name = "servicer_name")
	public String getServicerName() {
		return this.servicerName;
	}

	public void setServicerName(String servicerName) {
		this.servicerName = servicerName;
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
		return this.departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	@Column(name = "pre_pay_balance")
	public BigDecimal getPrePayBalance() {
		return this.prePayBalance;
	}

	public void setPrePayBalance(BigDecimal prePayBalance) {
		this.prePayBalance = prePayBalance;
	}

	@Column(name = "branch_id")
	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

}
