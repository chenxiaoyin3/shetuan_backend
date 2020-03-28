package com.hongyu.entity;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * HyOrderApplicationItem
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_order_application_item")
public class HyOrderApplicationItem implements java.io.Serializable {

	private Long id;
	private BigDecimal jiesuanRefund;
	private BigDecimal waimaiRefund;
	private Long itemId; //订单条目id
	private HyOrderApplication hyOrderApplication;
	
	private BigDecimal baoxianJiesuanRefund;
	private BigDecimal baoxianWaimaiRefund;
	
	private Integer returnQuantity;	//数量
	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "jiesuan_refund", precision = 10)
	public BigDecimal getJiesuanRefund() {
		return this.jiesuanRefund;
	}

	public void setJiesuanRefund(BigDecimal jiesuanRefund) {
		this.jiesuanRefund = jiesuanRefund;
	}

	@Column(name = "waimai_refund", precision = 10)
	public BigDecimal getWaimaiRefund() {
		return this.waimaiRefund;
	}

	public void setWaimaiRefund(BigDecimal waimaiRefund) {
		this.waimaiRefund = waimaiRefund;
	}

	@Column(name = "item_id")
	public Long getItemId() {
		return this.itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "application_id")
	public HyOrderApplication getHyOrderApplication() {
		return hyOrderApplication;
	}

	public void setHyOrderApplication(HyOrderApplication hyOrderApplication) {
		this.hyOrderApplication = hyOrderApplication;
	}



	@Column(name="baoxian_jiesuan_refund", precision = 10)
	public BigDecimal getBaoxianJiesuanRefund() {
		return baoxianJiesuanRefund;
	}

	public void setBaoxianJiesuanRefund(BigDecimal baoxianJiesuanRefund) {
		this.baoxianJiesuanRefund = baoxianJiesuanRefund;
	}

	@Column(name="baoxian_waimai_refund", precision = 10)
	public BigDecimal getBaoxianWaimaiRefund() {
		return baoxianWaimaiRefund;
	}

	public void setBaoxianWaimaiRefund(BigDecimal baoxianWaimaiRefund) {
		this.baoxianWaimaiRefund = baoxianWaimaiRefund;
	}

	@Column(name="return_quantity")
	public Integer getReturnQuantity() {
		return returnQuantity;
	}

	public void setReturnQuantity(Integer returnQuantity) {
		this.returnQuantity = returnQuantity;
	}



}
