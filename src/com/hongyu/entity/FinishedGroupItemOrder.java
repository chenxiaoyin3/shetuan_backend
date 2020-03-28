package com.hongyu.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
/**
 * 已完成的团的每单详情统计
 * @author liyang
 * @version 2019年5月8日 下午2:47:37
 */
@SuppressWarnings("serial")
@Entity
@Table(name="hy_finished_group_item_order")
public class FinishedGroupItemOrder implements Serializable{
	private Long id;
	/*团主键id*/
	private Long groupId;
	/*线路主键id*/
	private Long lineId;
	/*线路名称*/
	private String lineName;
	/*线路编号*/
	private String linePn;
	/*供应商名称*/
	private String hySupplier;
	/*是否内部供应商*/
	private Boolean isInner;
	/*订单id*/
	private Long orderId;
	/*订单编号*/
	private String orderNumber;
	/*发团日期*/
	private Date fatuantime;
	/*回团日期*/
	private Date huituantime;
	/*下单门店id*/
	private Long storeId;
	/*门店名称*/
	private String storeName;
	/*成人数量*/
	private Integer adultNumber;
	/*儿童数量*/
	private Integer childNumber;
	/*报名计调主键*/
	private String storeOperator;
	/*报名计调姓名*/
	private String storeOperatorName;
	/*接团计调主键*/
	private String supplier;
	/*接团计调姓名*/
	private String supplierName;
	/*订单收入*/
	private BigDecimal orderIncome;
	/*团的总收入*/
	private BigDecimal groupIncome;
	/*团的总支出*/
	private BigDecimal groupExpend;
	/*团的利润*/
	private BigDecimal groupProfit;
	/*团的人均利润*/
	private BigDecimal groupAverageProfit;
	/*团的利润率*/
	private BigDecimal groupProfitMargin;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id",nullable=false,unique=true)
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	@Column(name="group_id")
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	@Column(name="line_id")
	public Long getLineId() {
		return lineId;
	}
	public void setLineId(Long lineId) {
		this.lineId = lineId;
	}
	@Column(name="line_name")
	public String getLineName() {
		return lineName;
	}
	public void setLineName(String lineName) {
		this.lineName = lineName;
	}
	@Column(name="line_pn")
	public String getLinePn() {
		return linePn;
	}
	public void setLinePn(String linePn) {
		this.linePn = linePn;
	}
	@Column(name = "hy_supplier")
	public String getHySupplier(){
		return hySupplier;
	}
	public void setHySupplier(String hySupplier) {
		this.hySupplier = hySupplier;
	}
	@Column(name="is_inner")
	public Boolean getIsInner() {
		return isInner;
	}
	public void setIsInner(Boolean isInner) {
		this.isInner = isInner;
	}
	@Column(name="order_id")
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	@Column(name="order_number")
	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso=ISO.DATE)
	@Column(name="fatuan_time")
	public Date getFatuantime() {
		return fatuantime;
	}
	public void setFatuantime(Date fatuantime) {
		this.fatuantime = fatuantime;
	}
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso=ISO.DATE)
	@Column(name="huituan_time")
	public Date getHuituantime() {
		return huituantime;
	}
	public void setHuituantime(Date huituantime) {
		this.huituantime = huituantime;
	}
	@Column(name="store_id")
	public Long getStoreId() {
		return storeId;
	}
	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}
	@Column(name="store_name")
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	@Column(name="adult_number")
	public Integer getAdultNumber() {
		return adultNumber;
	}
	public void setAdultNumber(Integer adultNumber) {
		this.adultNumber = adultNumber;
	}
	@Column(name="child_number")
	public Integer getChildNumber() {
		return childNumber;
	}
	public void setChildNumber(Integer childNumber) {
		this.childNumber = childNumber;
	}
	@Column(name="store_operator")
	public String getStoreOperator() {
		return storeOperator;
	}
	public void setStoreOperator(String storeOperator) {
		this.storeOperator = storeOperator;
	}
	@Column(name="store_operator_name")
	public String getStoreOperatorName() {
		return storeOperatorName;
	}
	public void setStoreOperatorName(String storeOperatorName) {
		this.storeOperatorName = storeOperatorName;
	}
	@Column(name="supplier")
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	@Column(name="supplier_name")
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	@Column(name="order_income")
	public BigDecimal getOrderIncome() {
		return orderIncome;
	}
	public void setOrderIncome(BigDecimal orderIncome) {
		this.orderIncome = orderIncome;
	}
	@Column(name="group_income")
	public BigDecimal getGroupIncome() {
		return groupIncome;
	}
	public void setGroupIncome(BigDecimal groupIncome) {
		this.groupIncome = groupIncome;
	}
	@Column(name="group_expend")
	public BigDecimal getGroupExpend() {
		return groupExpend;
	}
	public void setGroupExpend(BigDecimal groupExpend) {
		this.groupExpend = groupExpend;
	}
	@Column(name="group_profit")
	public BigDecimal getGroupProfit() {
		return groupProfit;
	}
	public void setGroupProfit(BigDecimal groupProfit) {
		this.groupProfit = groupProfit;
	}
	@Column(name="group_average_profit")
	public BigDecimal getGroupAverageProfit() {
		return groupAverageProfit;
	}
	public void setGroupAverageProfit(BigDecimal groupAverageProfit) {
		this.groupAverageProfit = groupAverageProfit;
	}
	@Column(name="group_profit_margin")
	public BigDecimal getGroupProfitMargin() {
		return groupProfitMargin;
	}
	public void setGroupProfitMargin(BigDecimal groupProfitMargin) {
		this.groupProfitMargin = groupProfitMargin;
	}
	
}
