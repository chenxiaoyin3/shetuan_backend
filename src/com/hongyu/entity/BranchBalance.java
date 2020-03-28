package com.hongyu.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "hy_branch_balance")
public class BranchBalance {
	private Long id;
	/**hy_department表的id而不是hy_company表的id*/
	private Long branchId;
	private BigDecimal branchBalance;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "branch_id")
	public Long getBranchId() {
		return branchId;
	}
	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}
	
	@Column(name = "branch_balance")
	public BigDecimal getBranchBalance() {
		return branchBalance;
	}
	public void setBranchBalance(BigDecimal branchBalance) {
		this.branchBalance = branchBalance;
	}

}
