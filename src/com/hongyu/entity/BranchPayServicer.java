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

/** 门店增值业务付款*/
@Entity
@Table(name = "hy_branch_pay_servicer")
public class BranchPayServicer {
	private Long id;
	private Integer hasPaid;
	private Date applyDate;
	private String appliName;
	private Long servicerId;
	private String servicerName;
	private BigDecimal amount;
	private Long storeId;
	private Long branchId;
	private String remark;
	private Date payDate;
	private String operator;

	private Long AddedServiceTransferId;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "has_paid")
	public Integer getHasPaid() {
		return hasPaid;
	}

	public void setHasPaid(Integer hasPaid) {
		this.hasPaid = hasPaid;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "apply_date")
	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	@Column(name = "appli_name")
	public String getAppliName() {
		return appliName;
	}

	public void setAppliName(String appliName) {
		this.appliName = appliName;
	}

	@Column(name = "service_id")
	public Long getServicerId() {
		return servicerId;
	}

	public void setServicerId(Long servicerId) {
		this.servicerId = servicerId;
	}

	@Column(name = "service_name")
	public String getServicerName() {
		return servicerName;
	}

	public void setServicerName(String servicerName) {
		this.servicerName = servicerName;
	}

	@Column(name = "amount")
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name = "store_id")
	public Long getStoreId() {
		return storeId;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}

	@Column(name = "branch_id")
	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	@Column(name = "remark")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pay_date")
	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	@Column(name = "operator")
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	@Column(name = "added_service_transfer_id")
	public Long getAddedServiceTransferId() {
		return AddedServiceTransferId;
	}

	public void setAddedServiceTransferId(Long addedServiceTransferId) {
		AddedServiceTransferId = addedServiceTransferId;
	}
	
	
}
