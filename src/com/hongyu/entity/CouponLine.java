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
 * 线路赠送电子券
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_coupon_line")
public class CouponLine implements java.io.Serializable {

	private Long id;
	private String couponCode;
	private Date issueTime;
	private Date thawingTime; //解冻时间
	private Integer validityPeriod;
	private Float sum;
	private Integer state; //0:未绑定    1:已绑定     2:冻结   3:已过期
	private String receiver;
	private String receiverPhone;
	private String activationCode;
	private String bindPhone;
	private Date bindPhoneTime;
	private Long bindWechatAccountId;
	private Date bindWechatTime;
	private Date expireTime;
	private Long lineId;
	private String lineName;
	private Date startDate;
	private String issuer;
	private String issueDepartment;
	
	private Long groupId;
	private Boolean isValid;

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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "thawing_time")
	public Date getThawingTime() {
		return this.thawingTime;
	}

	public void setThawingTime(Date thawingTime) {
		this.thawingTime = thawingTime;
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
		return receiverPhone;
	}

	public void setReceiverPhone(String receiverPhone) {
		this.receiverPhone = receiverPhone;
	}

	
	@Column(name = "activation_code")
	public String getActivationCode() {
		return activationCode;
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

	
	@Column(name = "line_id")
	public Long getLineId() {
		return lineId;
	}

	public void setLineId(Long lineId) {
		this.lineId = lineId;
	}

	
	@Column(name = "line_name")
	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_date")
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Column(name = "issuer")
	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	@Column(name = "issue_department")
	public String getIssueDepartment() {
		return issueDepartment;
	}

	public void setIssueDepartment(String issueDepartment) {
		this.issueDepartment = issueDepartment;
	}

	@Column(name = "group_id")
	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	@Column(name="is_valid")
	public Boolean getIsValid() {
		return isValid;
	}

	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}
	
}
