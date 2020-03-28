package com.hongyu.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/** 商城销售电子券订单 */
@Entity
@Table(name = "hy_coupon_sale_order")
public class CouponSaleOrder implements java.io.Serializable {
	private Long id;
	private String orderCode;
	private String orderPhone;
	private Long orderWechatId;
	private Float totalMoney;
	private Float promotionAmount;
	private Float shouldPayMoney;
	private Integer orderState;
	private Date orderTime;
	private Date payTime;
	private Date orderCancelTime;
	private Long couponMoneyId;
	private Integer orderAmount;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "order_code")
	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	@Column(name = "order_phone")
	public String getOrderPhone() {
		return orderPhone;
	}

	public void setOrderPhone(String orderPhone) {
		this.orderPhone = orderPhone;
	}

	@Column(name = "order_wechar_id")
	public Long getOrderWechatId() {
		return orderWechatId;
	}

	public void setOrderWechatId(Long orderWechatId) {
		this.orderWechatId = orderWechatId;
	}

	@Column(name = "total_money")
	public Float getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(Float totalMoney) {
		this.totalMoney = totalMoney;
	}

	@Column(name = "promotion_amonut")
	public Float getPromotionAmount() {
		return promotionAmount;
	}

	public void setPromotionAmount(Float promotionAmount) {
		this.promotionAmount = promotionAmount;
	}

	@Column(name = "should_pay_money")
	public Float getShouldPayMoney() {
		return shouldPayMoney;
	}

	public void setShouldPayMoney(Float shouldPayMoney) {
		this.shouldPayMoney = shouldPayMoney;
	}

	@Column(name = "order_state")
	public Integer getOrderState() {
		return orderState;
	}

	public void setOrderState(Integer orderState) {
		this.orderState = orderState;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "order_time")
	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pay_time")
	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "order_cancel_time")
	public Date getOrderCancelTime() {
		return orderCancelTime;
	}

	public void setOrderCancelTime(Date orderCancelTime) {
		this.orderCancelTime = orderCancelTime;
	}

	@Column(name = "coupon_money_id")
	public Long getCouponMoneyId() {
		return couponMoneyId;
	}

	public void setCouponMoneyId(Long couponMoneyId) {
		this.couponMoneyId = couponMoneyId;
	}

	@Column(name = "order_amount")
	public Integer getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(Integer orderAmount) {
		this.orderAmount = orderAmount;
	}
	
	
}
