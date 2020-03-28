package com.hongyu.entity;

import static javax.persistence.GenerationType.IDENTITY;

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

/**
 * 应付款中间表
 * @author lbc
 * @version 2019.5.10
 */
@Entity
@Table(name = "hy_month_should_pay_intert")
public class MonthShouldPayInterT {
	Long id;
	//供应商名称
	String supplierName;
	//月初虹宇欠供应商金额
	BigDecimal monthStartMoney;
	//供应商欠虹宇总欠款
	BigDecimal debtMoney;
	//当月增加欠款
	BigDecimal monthIncreaseMoney;
	//当月减少欠款
	BigDecimal monthDecreaseMoney;
	//月末金额
	BigDecimal monthEndMoney;
	//从开始月份开始一个月的数据
	Date startMonth;
//	//所属地区
//	HyArea hyArea;
//	//门店类型
//	Integer storeType;//0虹宇门店，1挂靠门店，2直营门店，3非虹宇门店
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(name = "supplier_name")
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	@Column(name = "month_start_money")
	public BigDecimal getMonthStartMoney() {
		return monthStartMoney;
	}
	public void setMonthStartMoney(BigDecimal monthStartMoney) {
		this.monthStartMoney = monthStartMoney;
	}
	@Column(name = "debt_money")
	public BigDecimal getDebtMoney() {
		return debtMoney;
	}
	public void setDebtMoney(BigDecimal debtMoney) {
		this.debtMoney = debtMoney;
	}
	@Column(name = "month_increase_money")
	public BigDecimal getMonthIncreaseMoney() {
		return monthIncreaseMoney;
	}
	public void setMonthIncreaseMoney(BigDecimal monthIncreaseMoney) {
		this.monthIncreaseMoney = monthIncreaseMoney;
	}
	@Column(name = "month_decrease_money")
	public BigDecimal getMonthDecreaseMoney() {
		return monthDecreaseMoney;
	}
	public void setMonthDecreaseMoney(BigDecimal monthDecreaseMoney) {
		this.monthDecreaseMoney = monthDecreaseMoney;
	}
	@Column(name = "month_end_money")
	public BigDecimal getMonthEndMoney() {
		return monthEndMoney;
	}
	public void setMonthEndMoney(BigDecimal monthEndMoney) {
		this.monthEndMoney = monthEndMoney;
	}
	@Column(name = "start_month")
	public Date getStartMonth() {
		return startMonth;
	}
	public void setStartMonth(Date startMonth) {
		this.startMonth = startMonth;
	}
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "area")
//	public HyArea getHyArea() {
//		return hyArea;
//	}
//	public void setHyArea(HyArea hyArea) {
//		this.hyArea = hyArea;
//	}
//	@Column(name = "store_type")
//	public Integer getStoreType() {
//		return storeType;
//	}
//	public void setStoreType(Integer storeType) {
//		this.storeType = storeType;
//	}
	
	
	
	
}
