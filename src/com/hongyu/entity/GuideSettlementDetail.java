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
 * HyGuideSettlementDetail 
 */
@Entity
@Table(name = "hy_guide_settlement_detail")
public class GuideSettlementDetail implements java.io.Serializable {

	private Long id;
	private Long settlementId;
	private Long paiqianId;
	private Long groupId;
	private Long orderId;
	private Integer dispatchType;
	private Integer serviceType;
	private Date startDate;
	private String line;
	private Integer days;
	private String rentStore;
	private Long rentStoreId;
	private String regulate;
	private Integer number;
	private BigDecimal tip;
	private BigDecimal serviceFee;
	private BigDecimal deductFee;
	private BigDecimal accountPayable;
	private Boolean isCanSettle;//是否可结算
	private Long guiderId;  //导游id
	private Integer status; //是否结算

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "settlement_id")
	public Long getSettlementId() {
		return this.settlementId;
	}

	public void setSettlementId(Long settlementId) {
		this.settlementId = settlementId;
	}

	@Column(name = "group_id")
	public Long getGroupId() {
		return this.groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	@Column(name = "dispatch_type")
	public Integer getDispatchType() {
		return this.dispatchType;
	}

	public void setDispatchType(Integer dispatchType) {
		this.dispatchType = dispatchType;
	}

	@Column(name = "service_type")
	public Integer getServiceType() {
		return this.serviceType;
	}

	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "start_date", length = 10)
	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Column(name = "line")
	public String getLine() {
		return this.line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	@Column(name = "days")
	public Integer getDays() {
		return this.days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	@Column(name = "rent_store_id")
	public Long getRentStoreId() {
		return this.rentStoreId;
	}

	public void setRentStoreId(Long rentStoreId) {
		this.rentStoreId = rentStoreId;
	}

	@Column(name = "regulate")
	public String getRegulate() {
		return this.regulate;
	}

	public void setRegulate(String regulate) {
		this.regulate = regulate;
	}

	@Column(name = "number")
	public Integer getNumber() {
		return this.number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	@Column(name = "tip", precision = 10)
	public BigDecimal getTip() {
		return this.tip;
	}

	public void setTip(BigDecimal tip) {
		this.tip = tip;
	}

	@Column(name = "service_fee", precision = 10)
	public BigDecimal getServiceFee() {
		return this.serviceFee;
	}

	public void setServiceFee(BigDecimal serviceFee) {
		this.serviceFee = serviceFee;
	}

	@Column(name = "deduct_fee", precision = 10)
	public BigDecimal getDeductFee() {
		return this.deductFee;
	}

	public void setDeductFee(BigDecimal deductFee) {
		this.deductFee = deductFee;
	}

	@Column(name = "account_payable", precision = 10)
	public BigDecimal getAccountPayable() {
		return this.accountPayable;
	}

	public void setAccountPayable(BigDecimal accountPayable) {
		this.accountPayable = accountPayable;
	}
	@Column(name="paiqian_id")
	public Long getPaiqianId() {
		return paiqianId;
	}

	public void setPaiqianId(Long paiqianId) {
		this.paiqianId = paiqianId;
	}
	@Column(name="order_id")
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	@Column(name="rent_store")
	public String getRentStore() {
		return rentStore;
	}

	public void setRentStore(String rentStore) {
		this.rentStore = rentStore;
	}
	@Column(name="is_can_settle")
	public Boolean getIsCanSettle() {
		return isCanSettle;
	}

	public void setIsCanSettle(Boolean isCanSettle) {
		this.isCanSettle = isCanSettle;
	}

	@Column(name = "guider_id")
	public Long getGuiderId() {
		return guiderId;
	}

	public void setGuiderId(Long guiderId) {
		this.guiderId = guiderId;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	

}
