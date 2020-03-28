package com.hongyu.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;

import com.hongyu.util.Constants.AuditStatus;

/**
 * 财务中心-供应商保证金
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_deposit_supplier")
public class DepositSupplier implements java.io.Serializable {

	private Long id;

	private String supplierName;

	/** 供应商合同号 */
	private String contractCode;

	/** 金额 */
	@Digits(integer = 10, fraction = 2)
	private BigDecimal amount;

	/** 欠付金额 */
	@Digits(integer = 10, fraction = 2)
	private BigDecimal oweAmount;

	/** 交纳时间 */
	private Date payTime;

	/** 退还时间 */
	private Date refundTime;

	/** 合同状态 1:正常 2:变更续签 3:退出 */
	private Integer contractStatus;

	/** 备注 */
	private String remark;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "supplier_name")
	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	@Column(name = "contract_code")
	public String getContractCode() {
		return contractCode;
	}

	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}

	@Column(name = "amount")
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name = "owe_amount")
	public BigDecimal getOweAmount() {
		return oweAmount;
	}

	public void setOweAmount(BigDecimal oweAmount) {
		this.oweAmount = oweAmount;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pay_time", length = 19)
	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "refund_time", length = 19)
	public Date getRefundTime() {
		return this.refundTime;
	}

	public void setRefundTime(Date refundTime) {
		this.refundTime = refundTime;
	}

	@Column(name = "contract_status")
	public Integer getContractStatus() {
		return contractStatus;
	}

	public void setContractStatus(Integer contractStatus) {
		this.contractStatus = contractStatus;
	}

	@Column(name = "remark")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
