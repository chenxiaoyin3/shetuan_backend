package com.hongyu.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**二次消费-记录*/
@Entity
@Table(name = "hy_twice_consume_record")
public class TwiceConsumeRecord implements Serializable {
	private Long id;
	private Long wechat_id;
	private String consumer;
	private String phone;
	private String orderCode;
	private Float payment;
	private Float couponAmount;
	private Float wechatBalanceAmount;
	private Float cashAmount;
	private Date consumeTime;
	private Integer state; //0表示是首次使用电子券     1表示真正的二次消费


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "wechat_id")
	public Long getWechat_id() {
		return wechat_id;
	}

	public void setWechat_id(Long wechat_id) {
		this.wechat_id = wechat_id;
	}

	@Column(name = "consumer")
	public String getConsumer() {
		return this.consumer;
	}

	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}

	@Column(name = "phone")
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "order_code")
	public String getOrderCode() {
		return this.orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	@Column(name = "payment")
	public Float getPayment() {
		return this.payment;
	}

	public void setPayment(Float payment) {
		this.payment = payment;
	}

	@Column(name = "coupon_amount")
	public Float getCouponAmount() {
		return this.couponAmount;
	}

	public void setCouponAmount(Float couponAmount) {
		this.couponAmount = couponAmount;
	}

	@Column(name = "wechat_balance_amount")
	public Float getWechatBalanceAmount() {
		return this.wechatBalanceAmount;
	}

	public void setWechatBalanceAmount(Float wechatBalanceAmount) {
		this.wechatBalanceAmount = wechatBalanceAmount;
	}

	@Column(name = "cash_amount")
	public Float getCashAmount() {
		return this.cashAmount;
	}

	public void setCashAmount(Float cashAmount) {
		this.cashAmount = cashAmount;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "consume_time", length = 19)
	public Date getConsumeTime() {
		return this.consumeTime;
	}

	public void setConsumeTime(Date consumeTime) {
		this.consumeTime = consumeTime;
	}

	@Column(name = "state")
	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
	}
}
