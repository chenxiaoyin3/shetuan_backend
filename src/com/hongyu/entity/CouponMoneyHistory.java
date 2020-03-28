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

import com.hongyu.entity.CouponMoney;

/** 电子券折扣比例-历史 */
@Entity
@Table(name = "hy_coupon_money_history")
public class CouponMoneyHistory implements Serializable {

	private Long id;
	private Long pid;
	private Integer money;
	private String issueType;
	private Float rebateRatio;
	private String operator;
	private Date createTime;
	private Date expireTime;
	private Float couponCondition;
	
	//20180418 xyy 增加是否可叠加使用和分区id  只针对于商城赠送电子券
	/** 是否可叠加使用 */
	private Integer canOverlay;
	/** 商品分区id */
	private Long specialtyCategoryId;
	//20180424 在金额折扣历史表中增加 电子券的过期时间endTime
	private Date endTime;

	public CouponMoneyHistory(){
		
	}
	
	/**根据CouponMoney构建CouponMoneyHistory*/
	public CouponMoneyHistory(CouponMoney couponMoney) {
		this.pid = couponMoney.getId();
		this.money = couponMoney.getMoney();
		this.issueType = couponMoney.getIssueType();
		this.rebateRatio = couponMoney.getRebateRatio();
		this.operator = couponMoney.getOperator();
		this.createTime = couponMoney.getCreateTime();
		this.couponCondition = couponMoney.getCouponCondition();
		this.expireTime = new Date();
		this.canOverlay = couponMoney.getCanOverlay();
		this.specialtyCategoryId = couponMoney.getSpecialtyCategoryId();
		this.endTime = couponMoney.getEndTime();
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

	@Column(name = "money")
	public Integer getMoney() {
		return money;
	}

	public void setMoney(Integer money) {
		this.money = money;
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
	@Column(name = "expire_time", length = 19)
	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_time", length = 19)
	public Date getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
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

	@Column(name = "specialty_category_id")
	public Long getSpecialtyCategoryId() {
		return this.specialtyCategoryId;
	}

	public void setSpecialtyCategoryId(Long specialtyCategoryId) {
		this.specialtyCategoryId = specialtyCategoryId;
	}
}
