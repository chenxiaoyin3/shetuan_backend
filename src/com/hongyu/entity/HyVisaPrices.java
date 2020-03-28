package com.hongyu.entity;
// Generated 2017-12-24 21:20:19 by Hibernate Tools 3.6.0.Final

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

import org.springframework.format.annotation.DateTimeFormat;

/**
 * HyVisaPrices generated by hbm2java
 */
@Entity
@Table(name = "hy_visa_prices")
public class HyVisaPrices implements java.io.Serializable {

	private Long id;
	private HyVisa hyVisa; //visa_ID
	private Date startDate;
	private Date endDate;
	private BigDecimal displayPrice; //挂牌价
	private BigDecimal sellPrice; //外卖价
	private BigDecimal settlementPrice; //结算价
	
	//以下为门户用相关字段
	private BigDecimal mhDisplayPrice; //门户挂牌价
	private BigDecimal mhSellPrice; //门户外卖价
	private BigDecimal mhPrice; //官网销售价


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "visa_id")
	public HyVisa getHyVisa() {
		return this.hyVisa;
	}
	public void setHyVisa(HyVisa hyVisa) {
		this.hyVisa = hyVisa;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_date", length = 19)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getStartDate() {
		return this.startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_Date", length = 19)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getEndDate() {
		return this.endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Column(name = "display_price")
	public BigDecimal getDisplayPrice() {
		return this.displayPrice;
	}
	public void setDisplayPrice(BigDecimal displayPrice) {
		this.displayPrice = displayPrice;
	}

	@Column(name = "sell_price")
	public BigDecimal getSellPrice() {
		return this.sellPrice;
	}
	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}

	@Column(name = "settlement_price")
	public BigDecimal getSettlementPrice() {
		return this.settlementPrice;
	}
	public void setSettlementPrice(BigDecimal settlementPrice) {
		this.settlementPrice = settlementPrice;
	}
	
	@Column(name="mh_display_price")
	public BigDecimal getMhDisplayPrice() {
		return mhDisplayPrice;
	}
	public void setMhDisplayPrice(BigDecimal mhDisplayPrice) {
		this.mhDisplayPrice = mhDisplayPrice;
	}
	
	@Column(name="mh_sell_price")
	public BigDecimal getMhSellPrice() {
		return mhSellPrice;
	}
	public void setMhSellPrice(BigDecimal mhSellPrice) {
		this.mhSellPrice = mhSellPrice;
	}
	
	@Column(name="mh_price")
	public BigDecimal getMhPrice() {
		return mhPrice;
	}
	public void setMhPrice(BigDecimal mhPrice) {
		this.mhPrice = mhPrice;
	}
}