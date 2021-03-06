package com.hongyu.entity;
// Generated 2017-12-12 17:05:00 by Hibernate Tools 3.6.0.Final

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * HyInsurancePrice generated by hbm2java
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_insurance_price")
public class InsurancePrice implements java.io.Serializable {

	private Long id;
	private Integer day;
	private Integer startDay;
	private Integer endDay;
	private BigDecimal salePrice;
	private BigDecimal settlementPrice;

	private Insurance insurance;


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "day")
	public Integer getDay() {
		return this.day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}
	@Column(name="start_day")
	public Integer getStartDay() {
		return startDay;
	}

	public void setStartDay(Integer startDay) {
		this.startDay = startDay;
	}
	@Column(name="end_day")
	public Integer getEndDay() {
		return endDay;
	}

	public void setEndDay(Integer endDay) {
		this.endDay = endDay;
	}

	@Column(name = "sale_price", precision = 10)
	public BigDecimal getSalePrice() {
		return this.salePrice;
	}

	public void setSalePrice(BigDecimal salePrice) {
		this.salePrice = salePrice;
	}

	@Column(name = "settlement_price", precision = 10)
	public BigDecimal getSettlementPrice() {
		return this.settlementPrice;
	}

	public void setSettlementPrice(BigDecimal settlementPrice) {
		this.settlementPrice = settlementPrice;
	}

	@ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.REFRESH,optional = false)
	@JoinColumn(name="insurance_id")
	@JsonIgnore
	public Insurance getInsurance() {
		return insurance;
	}

	public void setInsurance(Insurance insurance) {
		this.insurance = insurance;
	}

}
