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
 * 电子券折扣比例
 */
@Entity
@Table(name = "hy_coupon_money")
public class CouponMoney implements java.io.Serializable {

	private Long id;
	private String issueType;
	private Integer money;
	private Float rebateRatio;
	private String operator;
	private Date createTime; 
	private Boolean isActive;
	private Float couponCondition;
	/** 是否可叠加使用 */
	private Integer canOverlay;

	/** 电子券的过期时间 */
	private Date endTime;

	/** 商品分区id */
	private Long specialtyCategoryId;

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

	@Column(name = "money")
	public Integer getMoney() {
		return money;
	}

	public void setMoney(Integer money) {
		this.money = money;
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

	@Column(name = "is_active")
	public Boolean getIsActive() {
		return this.isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	@Column(name = "coupon_condition")
	public Float getCouponCondition() {
		return couponCondition;
	}

	public void setCouponCondition(Float couponCondition) {
		this.couponCondition = couponCondition;
	}

	@Column(name = "can_overlay")
	public Integer getCanOverlay() {
		return canOverlay;
	}

	public void setCanOverlay(Integer canOverlay) {
		this.canOverlay = canOverlay;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_time")
	public Date getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Column(name = "specialty_category_id")
	public Long getSpecialtyCategoryId() {
		return this.specialtyCategoryId;
	}

	public void setSpecialtyCategoryId(Long specialtyCategoryId) {
		this.specialtyCategoryId = specialtyCategoryId;
	}
}
