package com.hongyu.entity;
// Generated 2018-1-2 16:59:39 by Hibernate Tools 3.6.0.Final

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 电子券折扣比例
 */
@Entity
@Table(name = "hy_coupon_rebate_ratio")
public class CouponRebateRatio implements java.io.Serializable {

	private Long id;
	private String issueType;
	private Float rebateRatio;
	private String operator;
	private Date createTime;
	private Date endTime;
	private Boolean isActive;

	public CouponRebateRatio() {
	}

	public CouponRebateRatio(long id) {
		this.id = id;
	}

	public CouponRebateRatio(long id, String issueType, Float rebateRatio, String operator, Date createTime,
			Date endTime, Boolean isActive) {
		this.id = id;
		this.issueType = issueType;
		this.rebateRatio = rebateRatio;
		this.operator = operator;
		this.createTime = createTime;
		this.endTime = endTime;
		this.isActive = isActive;
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

	@Column(name = "issue_type")
	public String getIssueType() {
		return this.issueType;
	}

	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}

	@Column(name = "rebate_ratio")
	public Float getRebateRatio() {
		return this.rebateRatio;
	}

	public void setRebateRatio(Float rebateRatio) {
		this.rebateRatio = rebateRatio;
	}

	@Column(name = "operator")
	public String getOperator() {
		return this.operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_time")
	public Date getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Column(name = "is_active")
	public Boolean getIsActive() {
		return this.isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	@PrePersist
	public void setPrepersist() {
		this.createTime = new Date();
	}

	@PreUpdate
	public void setPreupdate() {
		this.createTime = new Date();
	}
}
