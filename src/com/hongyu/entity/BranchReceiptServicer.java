package com.hongyu.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "hy_branch_receipt_servicer")
public class BranchReceiptServicer implements java.io.Serializable {
	private Long id;
	private int state;//0增加欠款1使用欠款
	private Long companyId; //hy_company id表示某一分公司
	private String operator;
	private BigDecimal amount;
	private Long orderOrSettleId; //state=0 表示orderid state=1 表示hy_payables_branchsettle id 代表是某一团结算抵扣
	private Date date;
	private BigDecimal balance;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "state")
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Column(name = "company_id")
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
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

	@Column(name = "order_or_settle_id")
	public Long getOrderOrSettleId() {
		return orderOrSettleId;
	}

	public void setOrderOrSettleId(Long orderOrSettleId) {
		this.orderOrSettleId = orderOrSettleId;
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

}
