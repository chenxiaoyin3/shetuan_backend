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

/** 线路应付款详情  20160615
 *  @author xyy
 * */
@Entity
@Table(name = "hy_payables_line_item")
public class PayablesLineItem {
	private Long id;
	private Long payablesLineId;
	private HyOrder hyOrder;
	private HySupplierContract supplierContract;
	/** 产品计调*/
	private HyAdmin operator;
	/** 1线路  2酒店  3门票  4酒加景 5签证  6认购门票*/
	private Integer productType;
	private HyGroup hyGroup;
	private Long ticketId;
	/** 产品编号*/
	private String sn;
	/** 产品名称  若为线路产品即为线路名称*/
	private String productName;
	/** 日期T 发团日期*/
	private Date tDate;
	/** 结算日期*/
	private Date settleDate;
	/** refunds没有数据的写入  orderMoney - koudian = money */
	private BigDecimal orderMoney;
	private BigDecimal refunds;
	private BigDecimal koudian;
	private BigDecimal money;
	/** 0 未提交  1 已提交 2已退款*/
	private Integer state;
	private Long paymentLineId;

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

	@DateTimeFormat(iso = ISO.DATE_TIME)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "t_date")
	public Date gettDate() {
		return tDate;
	}

	public void settDate(Date tDate) {
		this.tDate = tDate;
	}

	@DateTimeFormat(iso = ISO.DATE_TIME)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "settle_date")
	public Date getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(Date settleDate) {
		this.settleDate = settleDate;
	}

	@Column(name = "order_money")
	public BigDecimal getOrderMoney() {
		return orderMoney;
	}

	public void setOrderMoney(BigDecimal orderMoney) {
		this.orderMoney = orderMoney;
	}

	@Column(name = "refunds")
	public BigDecimal getRefunds() {
		return refunds;
	}

	public void setRefunds(BigDecimal refunds) {
		this.refunds = refunds;
	}

	@Column(name = "koudian")
	public BigDecimal getKoudian() {
		return koudian;
	}

	public void setKoudian(BigDecimal koudian) {
		this.koudian = koudian;
	}

	@Column(name = "money")
	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	@Column(name = "state")
	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	@Column(name = "payment_line_id")
	public Long getPaymentLineId() {
		return paymentLineId;
	}

	public void setPaymentLineId(Long paymentLineId) {
		this.paymentLineId = paymentLineId;
	}

}
