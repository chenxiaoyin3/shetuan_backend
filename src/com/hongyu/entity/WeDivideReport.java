package com.hongyu.entity;

import java.io.Serializable;
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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "hy_we_divide_report")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
public class WeDivideReport implements Serializable {
	private Long id;
	private WeBusiness weBusiness;
	private BigDecimal salesAmount;
	private BigDecimal divideAmount;
	private Date salesTime;
	private Date balanceTime;
	private Boolean transfered;
	private Date transferTime;
	
	public WeDivideReport() {
		
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", unique=true, nullable=false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "we_business_id")
	public WeBusiness getWeBusiness() {
		return weBusiness;
	}
	public void setWeBusiness(WeBusiness weBusiness) {
		this.weBusiness = weBusiness;
	}
	
	@Column(name="sales_amount", precision=10, scale=2)
	public BigDecimal getSalesAmount() {
		return salesAmount;
	}
	public void setSalesAmount(BigDecimal salesAmount) {
		this.salesAmount = salesAmount;
	}
	
	@Column(name="divide_amount", precision=10, scale=2)
	public BigDecimal getDivideAmount() {
		return divideAmount;
	}

	public void setDivideAmount(BigDecimal divideAmount) {
		this.divideAmount = divideAmount;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="sales_time")
	public Date getSalesTime() {
		return salesTime;
	}
	public void setSalesTime(Date salesTime) {
		this.salesTime = salesTime;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="balance_time")
	public Date getBalanceTime() {
		return balanceTime;
	}
	public void setBalanceTime(Date balanceTime) {
		this.balanceTime = balanceTime;
	}
	
	
	@Column(name="transfered")
	public Boolean getTransfered() {
		return transfered;
	}

	public void setTransfered(Boolean transfered) {
		this.transfered = transfered;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="transfer_time")
	public Date getTransferTime() {
		return transferTime;
	}

	public void setTransferTime(Date transferTime) {
		this.transferTime = transferTime;
	}

	@PrePersist
	public void Prepersist() {
		this.balanceTime = new Date();
		this.transfered = false;
	}
	
	
}
