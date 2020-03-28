package com.hongyu.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 售后推销团收支累计
 */
@Entity
@Table(name = "hy_receipt_total_servicer")
public class ReceiptTotalServicer implements java.io.Serializable {
	private Long id;
	private String supplierName;
	private BigDecimal balance;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	@Column(name = "balance")
	public BigDecimal getBalance() {
		return balance;
	}

	public String getSupplierName() {
		return supplierName;
	}

	@Column(name = "supplier_name")
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
	

	

}
