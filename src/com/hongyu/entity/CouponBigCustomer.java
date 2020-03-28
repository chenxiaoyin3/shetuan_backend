package com.hongyu.entity;
// Generated 2018-1-2 16:59:39 by Hibernate Tools 3.6.0.Final

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 大客户购买电子券
 */
@Entity
@Table(name = "hy_coupon_bigcustomer")
public class CouponBigCustomer implements java.io.Serializable {

	private Long id;
	private String couponCode;
	private Date issueTime;
	private Integer validityPeriod;
	private Float sum;
	private Float ratio;
	private Integer state;// 0:未绑定  1:已绑定
	private String receiver;
	private String receiverPhone;
	private String activationCode;
	private String bindPhone;
	private Date bindPhoneTime;
	private Long bindWechatAccountId;
	private Date bindWechatTime;
	private Date expireTime;
	private String saler;
	private Long couponBigCustomerAccountId;
	
	//20180426 xyy 短信查看 url后缀
	private String suffixurl;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
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

	
	@Column(name = "ratio")
	public Float getRatio() {
		return ratio;
	}

	public void setRatio(Float ratio) {
		this.ratio = ratio;
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
	@Column(name = "expire_time")
	public Date getExpireTime() {
		return this.expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	@Column(name = "saler")
	public String getSaler() {
		return saler;
	}

	public void setSaler(String saler) {
		this.saler = saler;
	}
	
	@Column(name = "coupon_bigcustomer_account_id")
	public Long getCouponBigCustomerAccountId() {
		return couponBigCustomerAccountId;
	}

	public void setCouponBigCustomerAccountId(Long couponBigCustomerAccountId) {
		this.couponBigCustomerAccountId = couponBigCustomerAccountId;
	}

	@Column(name = "suffix_url")
	public String getSuffixurl() {
		return suffixurl;
	}

	public void setSuffixurl(String suffixurl) {
		this.suffixurl = suffixurl;
	}

	
	
}
