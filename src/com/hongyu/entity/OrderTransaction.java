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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"
})
@Table(name = "hy_order_transaction")
public class OrderTransaction {
	private Long id;
	private String serialNum;
	private BusinessOrder businessOrder;
	private BigDecimal wechatBalance;
	private BigDecimal orderCoupon;
	private BigDecimal payment;
	private Integer payType;
	private String payAccount;
	private Integer payFlow;
	private Date payTime;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "serial_num")
	public String getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="order_id")
	public BusinessOrder getBusinessOrder() {
		return businessOrder;
	}
	public void setBusinessOrder(BusinessOrder businessOrder) {
		this.businessOrder = businessOrder;
	}
	
	@Column(name = "wechat_balance", precision=10, scale=2)
	public BigDecimal getWechatBalance() {
		return wechatBalance;
	}
	public void setWechatBalance(BigDecimal wechatBalance) {
		this.wechatBalance = wechatBalance;
	}
	
	@Column(name = "order_coupon", precision=10, scale=2)
	public BigDecimal getOrderCoupon() {
		return orderCoupon;
	}
	public void setOrderCoupon(BigDecimal orderCoupon) {
		this.orderCoupon = orderCoupon;
	}
	
	@Column(name = "payment", precision=10, scale=2)
	public BigDecimal getPayment() {
		return payment;
	}
	public void setPayment(BigDecimal payment) {
		this.payment = payment;
	}
	
	@Column(name = "pay_type")
	public Integer getPayType() {
		return payType;
	}
	public void setPayType(Integer payType) {
		this.payType = payType;
	}
	
	@Column(name = "pay_account")
	public String getPayAccount() {
		return payAccount;
	}
	public void setPayAccount(String payAccount) {
		this.payAccount = payAccount;
	}
	
	@Column(name = "pay_flow")
	public Integer getPayFlow() {
		return payFlow;
	}
	public void setPayFlow(Integer payFlow) {
		this.payFlow = payFlow;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pay_time", length = 19)
	public Date getPayTime() {
		return payTime;
	}
	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}
}
