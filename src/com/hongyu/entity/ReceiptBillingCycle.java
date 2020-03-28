package com.hongyu.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * ReceiptBillingCycle  总公司收款-分销商周期结算
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_receipt_billing_cycle")
public class ReceiptBillingCycle implements java.io.Serializable {

	private Long id;
	private Integer state;
	private Date applyDate;
	private String appliName;
	private String distributorName;
	private Date billingCycleStart;
	private Date billingCycleEnd;
	private BigDecimal amount;
	private Date date;
	private String receiver;



	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "state")
	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
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

	@Column(name = "distributor_name")
	public String getDistributorName() {
		return this.distributorName;
	}

	public void setDistributorName(String distributorName) {
		this.distributorName = distributorName;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "billing_cycle_start")
	public Date getBillingCycleStart() {
		return this.billingCycleStart;
	}

	public void setBillingCycleStart(Date billingCycleStart) {
		this.billingCycleStart = billingCycleStart;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "billing_cycle_end")
	public Date getBillingCycleEnd() {
		return this.billingCycleEnd;
	}

	public void setBillingCycleEnd(Date billingCycleEnd) {
		this.billingCycleEnd = billingCycleEnd;
	}

	@Column(name = "amount")
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date")
	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	@Column(name = "receiver")
	public String getReceiver() {
		return this.receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
}
