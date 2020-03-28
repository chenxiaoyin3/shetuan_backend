package com.hongyu.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"
	})
@Table(name = "hy_payables_branchsettle_refund")
public class PayablesBranchsettleRefund implements Serializable {
    private Long id;
    private Long branchsettleId;
    private Long orderId;
    private Long groupId;
    private Long orderApplicationId;
    
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="branch_settle_id")
	public Long getBranchsettleId() {
		return branchsettleId;
	}
	public void setBranchsettleId(Long branchsettleId) {
		this.branchsettleId = branchsettleId;
	}
	
	@Column(name="order_id")
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	
	@Column(name="group_id")
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	
	@Column(name="order_application_id")
	public Long getOrderApplicationId() {
		return orderApplicationId;
	}
	public void setOrderApplicationId(Long orderApplicationId) {
		this.orderApplicationId = orderApplicationId;
	}
}
