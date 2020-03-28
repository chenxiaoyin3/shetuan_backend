package com.hongyu.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 分公司 收款-门店管理费 - 收支记录
 */
@Entity
@Table(name = "hy_receipt_manage_fee_store")
public class ReceiptManageFeeStore implements java.io.Serializable {

	private Long id;
	private Long branchId;  //分公司id
	private Integer state;
	private String storeName;
	private String payer;
	private BigDecimal amount;
	private Date date;
	private String receiver;


	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "state")
	public Integer getState(){
		return this.state;
	}
	
	public void setState(Integer state){
		this.state = state;
	}
	
	@Column(name = "branch_id")
	public Long getBranchId() {
		return this.branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}


	@Column(name = "store_name" )
	public String getStoreName() {
		return this.storeName;
	}

	public void setstoreName(String storeName) {
		this.storeName = storeName;
	}

	@Column(name = "payer" )
	public String getPayer() {
		return this.payer;
	}

	public void setPayer(String payer) {
		this.payer = payer;
	}

	@Column(name = "amount")
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date" )
	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	@Column(name = "receiver")
	public String getReceiver() {
		return this.receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
}
