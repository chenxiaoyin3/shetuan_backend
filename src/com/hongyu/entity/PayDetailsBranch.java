package com.hongyu.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

/**
 * PayDetailsBranch  收支记录-分公司-付款
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_pay_details_branch")
public class PayDetailsBranch implements java.io.Serializable {

	private Long id;
	private Integer sort; // 1:BranchRecharge 2:BranchPayServicer 3:PayDepositBranch 4:receiptrefund
	private Long payId;
	private Long payMethod;
	private String account;
	private BigDecimal amount;
	private Date date;
	private String operator;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "sort")
	public Integer getSort() {
		return this.sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	@Column(name = "pay_id")
	public Long getPayId() {
		return this.payId;
	}

	public void setPayId(Long payId) {
		this.payId = payId;
	}

	@Column(name = "pay_method")
	public Long getPayMethod() {
		return this.payMethod;
	}

	public void setPayMethod(Long payMethod) {
		this.payMethod = payMethod;
	}

	@Column(name = "account")
	public String getAccount() {
		return this.account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Column(name = "amount")
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@DateTimeFormat(iso=ISO.DATE_TIME)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date", length = 19)
	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "operator")
	public String getOperator() {
		return this.operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

}
