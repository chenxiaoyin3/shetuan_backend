package com.hongyu.entity;
// Generated 2018-1-2 16:59:39 by Hibernate Tools 3.6.0.Final

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
 * HyCouponOnceUse generated by hbm2java
 */
@Entity
@Table(name = "hy_coupon_once_use")
public class CouponOnceUse implements java.io.Serializable {

	private Long id;
	private Long wechatId;
	private String orderCode;
	private Long couponId;
	private Float couponAmount;
	private Float useCouponAmount;
	private Date useTime;
	private Integer state;

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
	public Long getWechatId() {
		return wechatId;
	}

	public void setWechatId(Long wechatId) {
		this.wechatId = wechatId;
	}

	@Column(name = "order_code")
	public String getOrderCode() {
		return this.orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	@Column(name = "coupon_id")
	public Long getCouponId() {
		return this.couponId;
	}

	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}

	@Column(name = "coupon_amount")
	public Float getCouponAmount() {
		return this.couponAmount;
	}

	public void setCouponAmount(Float couponAmount) {
		this.couponAmount = couponAmount;
	}

	@Column(name = "use_coupon_amount")
	public Float getUseCouponAmount() {
		return useCouponAmount;
	}

	public void setUseCouponAmount(Float useCouponAmount) {
		this.useCouponAmount = useCouponAmount;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "use_time")
	public Date getUseTime() {
		return this.useTime;
	}

	public void setUseTime(Date useTime) {
		this.useTime = useTime;
	}

	@Column(name = "state")
	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

}
