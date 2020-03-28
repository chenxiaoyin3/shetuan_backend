package com.hongyu.entity;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"
	})
@Table(name = "hy_business_order_outbound")
public class BusinessOrderOutbound implements Serializable {
	private Long id;
	private BusinessOrderItem businessOrderItem;
	private Inbound inbound;
	private String depotCode;
	private Integer outboundQuantity;
	private Date outboundTime;
	private HyAdmin operator;
	private Integer returnQuantity;

	@Column(name = "is_valid")
	public Boolean getIsValid() {
		return isValid;
	}

	public void setIsValid(Boolean valid) {
		isValid = valid;
	}

	private Boolean isValid;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_item_id")
	public BusinessOrderItem getBusinessOrderItem() {
		return businessOrderItem;
	}
	public void setBusinessOrderItem(BusinessOrderItem businessOrderItem) {
		this.businessOrderItem = businessOrderItem;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inbound_id")
	public Inbound getInbound() {
		return inbound;
	}

	public void setInbound(Inbound inbound) {
		this.inbound = inbound;
	}

	@Column(name = "depot_code")
	public String getDepotCode() {
		return this.depotCode;
	}

	public void setDepotCode(String depotCode) {
		this.depotCode = depotCode;
	}

	@Column(name = "outbound_quantity")
	public Integer getOutboundQuantity() {
		return this.outboundQuantity;
	}

	public void setOutboundQuantity(Integer outboundQuantity) {
		this.outboundQuantity = outboundQuantity;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "outbound_time", length = 19)
	public Date getOutboundTime() {
		return this.outboundTime;
	}

	public void setOutboundTime(Date outboundTime) {
		this.outboundTime = outboundTime;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operator")
	public HyAdmin getOperator() {
		return this.operator;
	}

	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}

	@Column(name = "return_quantity")
	public Integer getReturnQuantity() {
		return this.returnQuantity;
	}

	public void setReturnQuantity(Integer returnQuantity) {
		this.returnQuantity = returnQuantity;
	}

	@PrePersist
	public void prePersist() {
		this.outboundTime = new Date();
		this.returnQuantity = 0;
		if(this.isValid == null){
			this.isValid = true;
		}
	}
	
}
