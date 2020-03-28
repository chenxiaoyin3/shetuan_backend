package com.hongyu.entity;
// Generated 2017-12-11 20:56:27 by Hibernate Tools 5.2.3.Final

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * SettlementConfirmDetail 
 * 分公司结算确认详情
 */
@Entity
@Table(name = "hy_settlement_confirm_detail")
public class SettlementConfirmDetail implements java.io.Serializable {

	private Long id;
	private Long settlementConfirmId;
	private Integer type;
	private String productId;
	private String lineName;
	private String operator;
	private BigDecimal incom;
	private BigDecimal dedectionPoint;
	private BigDecimal payout;
	private BigDecimal prePayAmount;
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

	@Column(name = "settlement_confirm_id", nullable = false)
	public Long getSettlementConfirmId() {
		return this.settlementConfirmId;
	}

	public void setSettlementConfirmId(Long settlementConfirmId) {
		this.settlementConfirmId = settlementConfirmId;
	}

	@Column(name = "type", nullable = false)
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "product_id", nullable = false)
	public String getProductId() {
		return this.productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	@Column(name = "line_name", nullable = false)
	public String getLineName() {
		return this.lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	@Column(name = "operator", nullable = false)
	public String getOperator() {
		return this.operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	@Column(name = "incom", nullable = false, precision = 21)
	public BigDecimal getIncom() {
		return this.incom;
	}

	public void setIncom(BigDecimal incom) {
		this.incom = incom;
	}

	@Column(name = "dedection_point", nullable = false, precision = 21)
	public BigDecimal getDedectionPoint() {
		return this.dedectionPoint;
	}

	public void setDedectionPoint(BigDecimal dedectionPoint) {
		this.dedectionPoint = dedectionPoint;
	}

	@Column(name = "payout", nullable = false, precision = 21)
	public BigDecimal getPayout() {
		return this.payout;
	}

	public void setPayout(BigDecimal payout) {
		this.payout = payout;
	}

	@Column(name = "pre_pay_amount", nullable = false, precision = 21)
	public BigDecimal getPrePayAmount() {
		return this.prePayAmount;
	}

	public void setPrePayAmount(BigDecimal prePayAmount) {
		this.prePayAmount = prePayAmount;
	}

	@Column(name = "amount", nullable = false, precision = 21)
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
