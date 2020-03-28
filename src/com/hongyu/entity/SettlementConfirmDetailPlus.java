package com.hongyu.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * SettlementConfirmDetailPlus
 * 分公司分成确认详情 Plus
 */
@Entity
@Table(name = "hy_settlement_confirm_detail_plus")
public class SettlementConfirmDetailPlus implements java.io.Serializable {

	private Long id;
	private Long settlementConfirmId;
	private Integer type;
	private String orderId;
	private String productId;
	private String lineName;
	private Date launchDate;
	private String contact;
	private Date refundDate;
	private String remark;
	private BigDecimal amount;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "settlement_confirm_id")
	public Long getSettlementConfirmId() {
		return this.settlementConfirmId;
	}

	public void setSettlementConfirmId(Long settlementConfirmId) {
		this.settlementConfirmId = settlementConfirmId;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "order_id")
	public String getOrderId() {
		return this.orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	@Column(name = "product_id")
	public String getProductId() {
		return this.productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	@Column(name = "line_name")
	public String getLineName() {
		return this.lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "launch_date")
	public Date getLaunchDate() {
		return this.launchDate;
	}

	public void setLaunchDate(Date launchDate) {
		this.launchDate = launchDate;
	}

	@Column(name = "contact")
	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "refund_date")
	public Date getRefundDate() {
		return this.refundDate;
	}

	public void setRefundDate(Date refundDate) {
		this.refundDate = refundDate;
	}

	@Column(name = "remark")
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "amount")
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
