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
 * 商城赠送
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_coupon_gift")
public class CouponGift implements java.io.Serializable {

	private Long id;
	private String couponCode;
	private Date issueTime;
	private Integer validityPeriod;
	private Float sum;
	private Integer state;//
	private String receiver;
	private String receiverPhone;
	private String activationCode;
	private String bindPhone;
	private Date bindPhoneTime;
	private Long bindWechatAccountId;
	private Date bindWechatTime;
	private Date useTime;
	private Date expireTime;
	private Float couponCondition;
	private String orderCode;
	private Float useCouponAmount;
	private Integer isValid;
	/** 是否可叠加使用 */
	private Integer canOverlay;
	/** CouponMoney表的id 使能判断用户是否已经领取该电子券 */
	private Long couponMoneyId;

	public CouponGift() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "coupon_code")
	public String getCouponCode() {
		return this.couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "issue_time")
	public Date getIssueTime() {
		return this.issueTime;
	}

	public void setIssueTime(Date issueTime) {
		this.issueTime = issueTime;
	}

	@Column(name = "validity_period")
	public Integer getValidityPeriod() {
		return this.validityPeriod;
	}

	public void setValidityPeriod(Integer validityPeriod) {
		this.validityPeriod = validityPeriod;
	}

	@Column(name = "sum")
	public Float getSum() {
		return this.sum;
	}

	public void setSum(Float sum) {
		this.sum = sum;
	}

	@Column(name = "receiver")
	public String getReceiver() {
		return this.receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	@Column(name = "receiver_phone")
	public String getReceiverPhone() {
		return this.receiverPhone;
	}

	public void setReceiverPhone(String receiverPhone) {
		this.receiverPhone = receiverPhone;
	}

	@Column(name = "activation_code")
	public String getActivationCode() {
		return this.activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	@Column(name = "bind_phone")
	public String getBindPhone() {
		return this.bindPhone;
	}

	public void setBindPhone(String bindPhone) {
		this.bindPhone = bindPhone;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "bind_phone_time")
	public Date getBindPhoneTime() {
		return this.bindPhoneTime;
	}

	public void setBindPhoneTime(Date bindPhoneTime) {
		this.bindPhoneTime = bindPhoneTime;
	}

	@Column(name = "bind_wechat_account_id")
	public Long getBindWechatAccountId() {
		return this.bindWechatAccountId;
	}

	public void setBindWechatAccountId(Long bindWechatAccountId) {
		this.bindWechatAccountId = bindWechatAccountId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "bind_wechat_time")
	public Date getBindWechatTime() {
		return this.bindWechatTime;
	}

	public void setBindWechatTime(Date bindWechatTime) {
		this.bindWechatTime = bindWechatTime;
	}

	@Column(name = "state")
	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "use_time")
	public Date getUseTime() {
		return this.useTime;
	}

	public void setUseTime(Date useTime) {
		this.useTime = useTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "expire_time")
	public Date getExpireTime() {
		return this.expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	@Column(name = "coupon_condition")
	public Float getCouponCondition() {
		return couponCondition;
	}

	public void setCouponCondition(Float couponCondition) {

		this.couponCondition = couponCondition;
	}

	@Column(name = "order_code")
	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	@Column(name = "use_coupon_amount")
	public Float getUseCouponAmount() {
		return useCouponAmount;
	}

	public void setUseCouponAmount(Float useCouponAmount) {
		this.useCouponAmount = useCouponAmount;
	}

	@Column(name = "is_valid")
	public Integer getIsValid() {
		return isValid;
	}

	public void setIsValid(Integer isValid) {
		this.isValid = isValid;
	}

	@Column(name = "can_overlay")
	public Integer getCanOverlay() {
		return canOverlay;
	}

	public void setCanOverlay(Integer canOverlay) {
		this.canOverlay = canOverlay;
	}

	@Column(name = "coupon_money_id")
	public Long getCouponMoneyId() {
		return couponMoneyId;
	}

	public void setCouponMoneyId(Long couponMoneyId) {
		this.couponMoneyId = couponMoneyId;
	}

}
