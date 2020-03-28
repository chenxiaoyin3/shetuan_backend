package com.hongyu.entity;
// Generated 2017-12-11 20:56:27 by Hibernate Tools 5.2.3.Final

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * ProfitShareConfirmDetail generated by hbm2java
 * 分公司分成确认单详情
 */
@Entity
@Table(name = "hy_profit_share_confirm_detail")
public class ProfitShareConfirmDetail implements java.io.Serializable {

	private Long id;
	private String orderCode;
	private HyOrder order;
	private String productId;
	private String productName;
	private Date date;
	private BigDecimal amount;
	private BigDecimal percentBranch;
	private BigDecimal shareProfit;
	private ProfitShareConfirm profitShareConfirmId;
	private Integer state;
	private Boolean isIncome;

	

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "order_code")
	public String getOrderCode() {
		return this.orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	
	@OneToOne
	@JoinColumn(name = "order_id")
	public HyOrder getOrder() {
		return order;
	}

	public void setOrder(HyOrder order) {
		this.order = order;
	}

	@Column(name = "product_id")
	public String getProductId() {
		return this.productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	@Column(name = "product_name")
	public String getProductName() {
		return this.productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "date")
	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "amount")
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name = "percent_branch")
	public BigDecimal getPercentBranch() {
		return this.percentBranch;
	}

	public void setPercentBranch(BigDecimal percentBranch) {
		this.percentBranch = percentBranch;
	}

	@Column(name = "share_profit")
	public BigDecimal getShareProfit() {
		return this.shareProfit;
	}

	public void setShareProfit(BigDecimal shareProfit) {
		this.shareProfit = shareProfit;
	}
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH, optional = false)
	@JoinColumn(name = "profit_share_confirm_id")
	public ProfitShareConfirm getProfitShareConfirmId() {
		return this.profitShareConfirmId;
	}

	public void setProfitShareConfirmId(ProfitShareConfirm profitShareConfirmId) {
		this.profitShareConfirmId = profitShareConfirmId;
	}

	@Column(name = "state")
	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
	}
	
	
	@Column(name = "is_income")
	public Boolean getIsIncome() {
		return isIncome;
	}

	public void setIsIncome(Boolean isIncome) {
		this.isIncome = isIncome;
	}

	@PrePersist
	public void setPrepersist() {
		this.state = 0;
		this.date = new Date();
	}

}