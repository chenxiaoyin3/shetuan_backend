package com.hongyu.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "hy_returned_inbound_detail")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ReturnedInboundDetail implements Serializable {
	private Long id;
	private BusinessOrder businessOrder;
	private BusinessOrderItem businessOrderItem;
	private Inbound inbound;
	private String depotCode;
	private Integer inboundQuantity;
	private Date inboundTime;
	private HyAdmin operator;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	public BusinessOrder getBusinessOrder() {
		return this.businessOrder;
	}

	public void setBusinessOrder(BusinessOrder businessOrder) {
		this.businessOrder = businessOrder;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_item_id")
	public BusinessOrderItem getBusinessOrderItem() {
		return this.businessOrderItem;
	}

	public void setBusinessOrderItem(BusinessOrderItem businessOrderItem) {
		this.businessOrderItem = businessOrderItem;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inbound_id")
	public Inbound getInbound() {
		return this.inbound;
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

	@Column(name = "inbound_quantity")
	public Integer getInboundQuantity() {
		return this.inboundQuantity;
	}

	public void setInboundQuantity(Integer inboundQuantity) {
		this.inboundQuantity = inboundQuantity;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "inbound_time", length = 19)
	public Date getInboundTime() {
		return this.inboundTime;
	}

	public void setInboundTime(Date inboundTime) {
		this.inboundTime = inboundTime;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operator")
	public HyAdmin getOperator() {
		return this.operator;
	}

	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}
}
