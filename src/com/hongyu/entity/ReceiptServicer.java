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
 * 售后退团欠款收支明细
 */
@Entity
@Table(name = "hy_receipt_servicer")
public class ReceiptServicer {
	
	private Long id;
	private String supplierName;
	private int state;  //0增加欠款  1使用欠款
	private String operator;  //操作人
	private BigDecimal amount; //此次钱数
	private Long orderOrPayServicerId;  //state=0:orderId  state=1:payServicerId
	private Date date;  //操作时间
	private BigDecimal balance; //余额

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
    @Column(name = "supplier_name")
	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	@Column(name = "state")
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Column(name = "operator")
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}



	@Column(name = "amount")
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name = "date")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "balance")
	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	@Column (name = "order_or_payservicer_id")
	public Long getOrderOrPayServicerId() {
		return orderOrPayServicerId;
	}

	public void setOrderOrPayServicerId(Long orderOrPayServicerId) {
		this.orderOrPayServicerId = orderOrPayServicerId;
	}
	
	
	

}
