package com.hongyu.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "hy_payment_element_item")
public class PaymentElementItem {
	private Long id;
	private Long groupId;
	private Long payablesElementId;
	private Long paymentElementId;
	private BigDecimal money;
	private BigDecimal payPrePay; // 本次本团使用预付款金额
	private BigDecimal pay;
	private Integer status;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "group_id")
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	
	@Column(name = "payables_element_id")
	public Long getPayablesElementId() {
		return payablesElementId;
	}
	public void setPayablesElementId(Long payablesElementId) {
		this.payablesElementId = payablesElementId;
	}
	
	@Column(name = "payment_element_id")
	public Long getPaymentElementId() {
		return paymentElementId;
	}
	public void setPaymentElementId(Long paymentElementId) {
		this.paymentElementId = paymentElementId;
	}
	
	@Column(name = "money")
	public BigDecimal getMoney() {
		return money;
	}
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	
	@Column(name = "pay_pre_pay")
	public BigDecimal getPayPrePay() {
		return payPrePay;
	}
	public void setPayPrePay(BigDecimal payPrePay) {
		this.payPrePay = payPrePay;
	}
	
	@Column(name = "pay")
	public BigDecimal getPay() {
		return pay;
	}
	public void setPay(BigDecimal pay) {
		this.pay = pay;
	}
	
	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
