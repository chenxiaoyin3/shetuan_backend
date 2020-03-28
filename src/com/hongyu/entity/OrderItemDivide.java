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
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.hongyu.Order;

@Entity
@Table(name="order_item_divide")
public class OrderItemDivide {

	private Long id;
	private Date ordertime;
	private Date acceptTime;
	private BusinessOrderItem businessOrderItem;
	private BusinessOrder businessOrder;
	private BigDecimal totalAmount;
	private WeBusiness weBusiness;
	private BigDecimal weBusinessAmount;
	private WeBusiness rWeBusiness;
	private BigDecimal rWeBusinessAmount;
	private WeBusiness mWeBusiness;
	private BigDecimal mWeBusinessAmount;
	private Boolean transfered;
	private Date transferTime;
	private Boolean isvalid;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "ordertime")
	public Date getOrdertime() {
		return ordertime;
	}

	public void setOrdertime(Date ordertime) {
		this.ordertime = ordertime;
	}

	@Column(name = "accept_time")
	public Date getAcceptTime() {
		return acceptTime;
	}

	public void setAcceptTime(Date acceptTime) {
		this.acceptTime = acceptTime;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_item_id")
	public BusinessOrderItem getBusinessOrderItem() {
		return businessOrderItem;
	}

	public void setBusinessOrderItem(BusinessOrderItem businessOrderItem) {
		this.businessOrderItem = businessOrderItem;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	public BusinessOrder getBusinessOrder() {
		return businessOrder;
	}

	public void setBusinessOrder(BusinessOrder businessOrder) {
		this.businessOrder = businessOrder;
	}

	@Column(name = "total_amount")
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "we_business_id")
	public WeBusiness getWeBusiness() {
		return weBusiness;
	}

	public void setWeBusiness(WeBusiness weBusiness) {
		this.weBusiness = weBusiness;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "r_we_business_id")
	public WeBusiness getrWeBusiness() {
		return rWeBusiness;
	}

	public void setrWeBusiness(WeBusiness rWeBusiness) {
		this.rWeBusiness = rWeBusiness;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "m_we_business_id")
	public WeBusiness getmWeBusiness() {
		return mWeBusiness;
	}

	public void setmWeBusiness(WeBusiness mWeBusiness) {
		this.mWeBusiness = mWeBusiness;
	}

	@Column(name = "we_business_amount")
	public BigDecimal getWeBusinessAmount() {
		return weBusinessAmount;
	}

	public void setWeBusinessAmount(BigDecimal weBusinessAmount) {
		this.weBusinessAmount = weBusinessAmount;
	}

	@Column(name = "r_we_business_amount")
	public BigDecimal getrWeBusinessAmount() {
		return rWeBusinessAmount;
	}

	public void setrWeBusinessAmount(BigDecimal rWeBusinessAmount) {
		this.rWeBusinessAmount = rWeBusinessAmount;
	}

	@Column(name = "m_we_business_amount")
	public BigDecimal getmWeBusinessAmount() {
		return mWeBusinessAmount;
	}

	public void setmWeBusinessAmount(BigDecimal mWeBusinessAmount) {
		this.mWeBusinessAmount = mWeBusinessAmount;
	}

	@Column(name = "transfered")
	public Boolean getTransfered() {
		return transfered;
	}

	public void setTransfered(Boolean transfered) {
		this.transfered = transfered;
	}

	@Column(name = "transfer_time")
	public Date getTransferTime() {
		return transferTime;
	}

	public void setTransferTime(Date transferTime) {
		this.transferTime = transferTime;
	}

	@Column(name = "isvalid")
	public Boolean getIsvalid() {
		return isvalid;
	}

	public void setIsvalid(Boolean isvalid) {
		this.isvalid = isvalid;
	}
	
	@PrePersist
	public void setPrepersist() {
//		this.acceptTime = new Date();
		this.transfered = false;
		this.isvalid = true;
	}
}
