package com.hongyu.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/** 电子券折扣比例-历史 */
@Entity
@Table(name = "hy_coupon_rebate_ratio_history")
public class CouponRebateRatioHistory implements Serializable {

	private Long id;
	private Long pid;
	private String issueType;
	private Float rebateRatio;
	private String operator;
	private Date createTime;
	private Date endTime;
	private Boolean isActive;

	public CouponRebateRatioHistory() {
	}

	public CouponRebateRatioHistory(CouponRebateRatio couponRatio) {
		this.pid = couponRatio.getId();
		this.issueType = couponRatio.getIssueType();
		this.rebateRatio = couponRatio.getRebateRatio();
		this.operator = couponRatio.getOperator();
		this.createTime = couponRatio.getCreateTime();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "pid")
	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	@Column(name = "issue_type")
	public String getIssueType() {
		return issueType;
	}

	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}

	@Column(name = "rebate_ratio")
	public Float getRebateRatio() {
		return rebateRatio;
	}

	public void setRebateRatio(Float rebateRatio) {
		this.rebateRatio = rebateRatio;
	}

	@Column(name = "operator")
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", length = 19)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_time", length = 19)
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Column(name = "is_active")
	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	@PrePersist
	public void setPrepersist() {
		this.endTime = new Date();
	}

}
