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

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

/** 退款详情 20160615*/
@Entity
@Table(name = "hy_payables_refund_item")
public class PayablesRefundItem {
	private Long id;
	private Long payablesLineId;
	private HyOrder hyOrder;
	private HySupplierContract supplierContract;
	private HyAdmin operator; // 产品计调
	private Integer productType;
	private HyGroup hyGroup;
	private Long ticketId;
	private Date refundDate;
	private BigDecimal refundMoney;
	private String tourist; // 退款游客
	private String remark;
	private Integer state;
	private Long paymentLineId;
	
	private String sn; // 产品编号
	private String productName; //产品名称

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "payables_line_id")
	public Long getPayablesLineId() {
		return payablesLineId;
	}

	public void setPayablesLineId(Long payablesLineId) {
		this.payablesLineId = payablesLineId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	public HyOrder getHyOrder() {
		return hyOrder;
	}

	public void setHyOrder(HyOrder hyOrder) {
		this.hyOrder = hyOrder;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contract")
	public HySupplierContract getSupplierContract() {
		return supplierContract;
	}

	public void setSupplierContract(HySupplierContract supplierContract) {
		this.supplierContract = supplierContract;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "operator")
	public HyAdmin getOperator() {
		return operator;
	}

	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}

	@Column(name = "product_type")
	public Integer getProductType() {
		return productType;
	}

	public void setProductType(Integer productType) {
		this.productType = productType;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	public HyGroup getHyGroup() {
		return hyGroup;
	}

	public void setHyGroup(HyGroup hyGroup) {
		this.hyGroup = hyGroup;
	}

	@Column(name = "ticket_id")
	public Long getTicketId() {
		return ticketId;
	}

	public void setTicketId(Long ticketId) {
		this.ticketId = ticketId;
	}

	@DateTimeFormat(iso = ISO.DATE_TIME)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "refund_date")
	public Date getRefundDate() {
		return refundDate;
	}

	public void setRefundDate(Date refundDate) {
		this.refundDate = refundDate;
	}

	@Column(name = "refund_money")
	public BigDecimal getRefundMoney() {
		return refundMoney;
	}

	public void setRefundMoney(BigDecimal refundMoney) {
		this.refundMoney = refundMoney;
	}

	@Column(name = "tourist")
	public String getTourist() {
		return tourist;
	}

	public void setTourist(String tourist) {
		this.tourist = tourist;
	}

	@Column(name = "remark")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "state")
	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	@Column(name = "payment_line")
	public Long getPaymentLineId() {
		return paymentLineId;
	}

	public void setPaymentLineId(Long paymentLineId) {
		this.paymentLineId = paymentLineId;
	}

	@Column(name = "sn")
	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	@Column(name = "product_name")
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
}
