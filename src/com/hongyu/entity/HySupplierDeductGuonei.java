package com.hongyu.entity;
// default package
// Generated 2018-1-12 17:38:27 by Hibernate Tools 3.6.0.Final

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hongyu.util.Constants.DeductLine;

/**
 * HySupplierDeductGuonei generated by hbm2java
 */
@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "hy_supplier_deduct_guonei")
public class HySupplierDeductGuonei implements java.io.Serializable {

	private Long id;
	private DeductLine deductGuonei;
	
	@Digits(integer=10, fraction=2)
	private BigDecimal sankeGuonei;
	
	@Digits(integer=10, fraction=2)
	private BigDecimal tuankeGuonei;
	
	@Digits(integer=10, fraction=2)
	private BigDecimal rentouGuonei;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "deduct_guonei", nullable = false)
	public DeductLine getDeductGuonei() {
		return this.deductGuonei;
	}

	public void setDeductGuonei(DeductLine deductGuonei) {
		this.deductGuonei = deductGuonei;
	}

	@Column(name = "sanke_guonei", precision = 10, scale = 0)
	public BigDecimal getSankeGuonei() {
		return this.sankeGuonei;
	}

	public void setSankeGuonei(BigDecimal sankeGuonei) {
		this.sankeGuonei = sankeGuonei;
	}

	@Column(name = "tuanke_guonei", precision = 10, scale = 0)
	public BigDecimal getTuankeGuonei() {
		return this.tuankeGuonei;
	}

	public void setTuankeGuonei(BigDecimal tuankeGuonei) {
		this.tuankeGuonei = tuankeGuonei;
	}

	@Column(name = "rentou_guonei", precision = 10, scale = 0)
	public BigDecimal getRentouGuonei() {
		return this.rentouGuonei;
	}

	public void setRentouGuonei(BigDecimal rentouGuonei) {
		this.rentouGuonei = rentouGuonei;
	}

}