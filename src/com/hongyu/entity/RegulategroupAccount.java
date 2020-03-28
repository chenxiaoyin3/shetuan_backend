package com.hongyu.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * 单团核算表
 * @author guoxinze
 *
 */
@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
	"groupId",
	"regulateId"
	})
@Table(name = "hy_regulategroup_account")
public class RegulategroupAccount implements Serializable {
	private Long id;
	private HyRegulate regulateId;
	private HyGroup groupId;
	private String lineSn;
	private String lineName;
	private Integer days;
	private Date startDate;
	private Date endDate;
	private String operatorName;
	private Integer visitorNo;
	private String guide;
	@Digits(integer=10, fraction=2)
	private BigDecimal groupMoney;
	@Digits(integer=10, fraction=2)
	private BigDecimal shopping;
	@Digits(integer=10, fraction=2)
	private BigDecimal ticket;
	@Digits(integer=10, fraction=2)
	private BigDecimal selfExpense;
	@Digits(integer=10, fraction=2)
	private BigDecimal incomes;
	@Digits(integer=10, fraction=2)
	private BigDecimal guidePrice;
	@Digits(integer=10, fraction=2)
	private BigDecimal restaurant;
	@Digits(integer=10, fraction=2)
	private BigDecimal vehicle;
	@Digits(integer=10, fraction=2)
	private BigDecimal traffic;
	@Digits(integer=10, fraction=2)
	private BigDecimal hotel;
	@Digits(integer=10, fraction=2)
	private BigDecimal insurance;
	@Digits(integer=10, fraction=2)
	private BigDecimal coupon;
	@Digits(integer=10, fraction=2)
	private BigDecimal outgoings;
	@Digits(integer=10, fraction=2)
	private BigDecimal dijie;
	@Digits(integer=10, fraction=2)
	private BigDecimal allIncome;
	@Digits(integer=10, fraction=2)
	private BigDecimal allExpense;
	@Digits(integer=10, fraction=2)
	private BigDecimal profit;
	@Digits(integer=10, fraction=2)
	private BigDecimal averageProfit;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "regulate_id")
	public HyRegulate getRegulateId() {
		return regulateId;
	}
	public void setRegulateId(HyRegulate regulateId) {
		this.regulateId = regulateId;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	public HyGroup getGroupId() {
		return groupId;
	}
	public void setGroupId(HyGroup groupId) {
		this.groupId = groupId;
	}
	public String getLineSn() {
		return lineSn;
	}
	public void setLineSn(String lineSn) {
		this.lineSn = lineSn;
	}
	public String getLineName() {
		return lineName;
	}
	public void setLineName(String lineName) {
		this.lineName = lineName;
	}
	public Integer getDays() {
		return days;
	}
	public void setDays(Integer days) {
		this.days = days;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "start_date", length = 19)
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "end_date", length = 19)
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public Integer getVisitorNo() {
		return visitorNo;
	}
	public void setVisitorNo(Integer visitorNo) {
		this.visitorNo = visitorNo;
	}
	public String getGuide() {
		return guide;
	}
	public void setGuide(String guide) {
		this.guide = guide;
	}
	public BigDecimal getGroupMoney() {
		return groupMoney;
	}
	public void setGroupMoney(BigDecimal groupMoney) {
		this.groupMoney = groupMoney;
	}
	public BigDecimal getShopping() {
		return shopping;
	}
	public void setShopping(BigDecimal shopping) {
		this.shopping = shopping;
	}
	public BigDecimal getSelfExpense() {
		return selfExpense;
	}
	public void setSelfExpense(BigDecimal selfExpense) {
		this.selfExpense = selfExpense;
	}
	public BigDecimal getIncomes() {
		return incomes;
	}
	public void setIncomes(BigDecimal incomes) {
		this.incomes = incomes;
	}
	public BigDecimal getGuidePrice() {
		return guidePrice;
	}
	public void setGuidePrice(BigDecimal guidePrice) {
		this.guidePrice = guidePrice;
	}
	public BigDecimal getRestaurant() {
		return restaurant;
	}
	public void setRestaurant(BigDecimal restaurant) {
		this.restaurant = restaurant;
	}
	public BigDecimal getVehicle() {
		return vehicle;
	}
	public void setVehicle(BigDecimal vehicle) {
		this.vehicle = vehicle;
	}
	public BigDecimal getTraffic() {
		return traffic;
	}
	public void setTraffic(BigDecimal traffic) {
		this.traffic = traffic;
	}
	public BigDecimal getHotel() {
		return hotel;
	}
	public void setHotel(BigDecimal hotel) {
		this.hotel = hotel;
	}
	public BigDecimal getInsurance() {
		return insurance;
	}
	public void setInsurance(BigDecimal insurance) {
		this.insurance = insurance;
	}
	public BigDecimal getOutgoings() {
		return outgoings;
	}
	public void setOutgoings(BigDecimal outgoings) {
		this.outgoings = outgoings;
	}
	public BigDecimal getDijie() {
		return dijie;
	}
	public void setDijie(BigDecimal dijie) {
		this.dijie = dijie;
	}
	public BigDecimal getAllIncome() {
		return allIncome;
	}
	public void setAllIncome(BigDecimal allIncome) {
		this.allIncome = allIncome;
	}
	public BigDecimal getAllExpense() {
		return allExpense;
	}
	public void setAllExpense(BigDecimal allExpense) {
		this.allExpense = allExpense;
	}
	public BigDecimal getProfit() {
		return profit;
	}
	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}
	public BigDecimal getAverageProfit() {
		return averageProfit;
	}
	public void setAverageProfit(BigDecimal averageProfit) {
		this.averageProfit = averageProfit;
	}
	public BigDecimal getCoupon() {
		return coupon;
	}
	public void setCoupon(BigDecimal coupon) {
		this.coupon = coupon;
	}
	public BigDecimal getTicket() {
		return ticket;
	}
	public void setTicket(BigDecimal ticket) {
		this.ticket = ticket;
	}
	
	
}
