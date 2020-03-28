package com.hongyu.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * AddedServiceTransfer 增值业务申请付款
 */
@Entity
@Table(name = "hy_added_service_transfer")
public class AddedServiceTransfer {

	private Long id;
	private Long storeId;
	private HyAddedServiceSupplier supplier;
	private BigDecimal money;
	private HyAdmin operator;
	/** 申请日期*/
	private Date createTime; 
	private String processInstanceId;
	/** 0 未审核-未付(已驳回-未付) 1审核中-未付 2 已通过-未付 3已通过-已付 */
	private Integer status; 
	/** 审核步骤   1:待门店经理审核  2:待分公司副总(限额)审核  3:待分公司财务审核*/
	private Integer step;  
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "store_id")
	public Long getStoreId() {
		return this.storeId;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supplier_id")
	public HyAddedServiceSupplier getSupplier() {
		return this.supplier;
	}

	public void setSupplier(HyAddedServiceSupplier supplier) {
		this.supplier = supplier;
	}

	@Column(name = "money")
	public BigDecimal getMoney() {
		return this.money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operator_id")
	public HyAdmin getOperator() {
		return this.operator;
	}

	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "process_instance_id")
	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "step")
	public Integer getStep() {
		return step;
	}

	public void setStep(Integer step) {
		this.step = step;
	}

	
	
}
