package com.hongyu.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 产品中心付尾款申请 - 条目
 *
 * @author xyy
 */
@Entity
@Table(name = "hy_balance_due_apply_item")
public class BalanceDueApplyItem {
	private Long id;
	private Long balanceDueApplyId;
	private Long groupId;
	private String productId;
	private Date launchDate;
	private String lineName;
	/** 计调*/
	private HyAdmin operator;
	private Integer amount;
	private BigDecimal usePrePay;
	private BigDecimal payMoney;
	/** 申请人*/
	private HyAdmin applier;
	/** 申请日期*/
	private Date createTime;
	/** 付款状态 0 1*/
	private Integer payStatus;

	private Long hyPayablesElementId;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "balance_due_apply_id")
	public Long getBalanceDueApplyId() {
		return balanceDueApplyId;
	}

	public void setBalanceDueApplyId(Long balanceDueApplyId) {
		this.balanceDueApplyId = balanceDueApplyId;
	}

	@Column(name = "group_id")
	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	@Column(name = "product_id")
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "launch_date")
	public Date getLaunchDate() {
		return launchDate;
	}

	public void setLaunchDate(Date launchDate) {
		this.launchDate = launchDate;
	}

	@Column(name = "line_name")
	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operator")
	public HyAdmin getOperator() {
		return operator;
	}

	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}

	@Column(name = "amount")
	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	@Column(name = "use_pre_pay")
	public BigDecimal getUsePrePay() {
		return usePrePay;
	}

	public void setUsePrePay(BigDecimal usePrePay) {
		this.usePrePay = usePrePay;
	}

	@Column(name = "pay_money")
	public BigDecimal getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(BigDecimal payMoney) {
		this.payMoney = payMoney;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "applier")
	public HyAdmin getApplier() {
		return applier;
	}

	public void setApplier(HyAdmin applier) {
		this.applier = applier;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "pay_status")
	public Integer getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}

	@Column(name = "payables_element_id")
	public Long getHyPayablesElementId() {
		return hyPayablesElementId;
	}

	public void setHyPayablesElementId(Long hyPayablesElementId) {
		this.hyPayablesElementId = hyPayablesElementId;
	}

}
