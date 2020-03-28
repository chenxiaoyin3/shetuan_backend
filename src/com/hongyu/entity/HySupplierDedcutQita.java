package com.hongyu.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hongyu.util.Constants.DeductQita;
@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "hy_supplier_deduct_qita")
public class HySupplierDedcutQita implements Serializable {
	private Long id;
	private DeductQita deductQita;
	
	@Digits(integer=10, fraction=2)
	private BigDecimal liushuiQita;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DeductQita getDeductQita() {
		return deductQita;
	}

	public void setDeductQita(DeductQita deductQita) {
		this.deductQita = deductQita;
	}

	public BigDecimal getLiushuiQita() {
		return liushuiQita;
	}

	public void setLiushuiQita(BigDecimal liushuiQita) {
		this.liushuiQita = liushuiQita;
	}
	
}
