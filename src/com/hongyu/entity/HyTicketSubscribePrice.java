package com.hongyu.entity;

import java.io.Serializable;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
						})
@Table(name = "hy_ticket_subscribe_price")
public class HyTicketSubscribePrice implements Serializable {
    private Long id;
    
    private HyTicketSubscribe ticketSubscribe;
    private Date startDate;
    private Date endDate;
    private BigDecimal adultListPrice; //成人挂牌价
    private BigDecimal adultOutsalePrice; //成人外卖价
    private BigDecimal adultSettlePrice; //成人结算价
    private BigDecimal childListPrice;
    private BigDecimal childOutPrice;
    private BigDecimal childSettlePrice;
    private BigDecimal studentListPrice;
    private BigDecimal studentOutsalePrice;
    private BigDecimal studentSettlePrice;
    private BigDecimal oldListPrice;
    private BigDecimal oldOutsalePrice;
    private BigDecimal oldSettlePrice;
    private Integer inventory; //存货数量
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="subscribe")
	public HyTicketSubscribe getTicketSubscribe() {
		return ticketSubscribe;
	}
	public void setTicketSubscribe(HyTicketSubscribe ticketSubscribe) {
		this.ticketSubscribe = ticketSubscribe;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_date")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_date")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@Column(name="adult_list_price")
	public BigDecimal getAdultListPrice() {
		return adultListPrice;
	}
	public void setAdultListPrice(BigDecimal adultListPrice) {
		this.adultListPrice = adultListPrice;
	}
	
	@Column(name="adult_outsale_price")
	public BigDecimal getAdultOutsalePrice() {
		return adultOutsalePrice;
	}
	public void setAdultOutsalePrice(BigDecimal adultOutsalePrice) {
		this.adultOutsalePrice = adultOutsalePrice;
	}
	
	@Column(name="adult_settle_price")
	public BigDecimal getAdultSettlePrice() {
		return adultSettlePrice;
	}
	public void setAdultSettlePrice(BigDecimal adultSettlePrice) {
		this.adultSettlePrice = adultSettlePrice;
	}
	
	@Column(name="child_list_price")
	public BigDecimal getChildListPrice() {
		return childListPrice;
	}
	public void setChildListPrice(BigDecimal childListPrice) {
		this.childListPrice = childListPrice;
	}
	
	@Column(name="child_outsale_price")
	public BigDecimal getChildOutPrice() {
		return childOutPrice;
	}
	public void setChildOutPrice(BigDecimal childOutPrice) {
		this.childOutPrice = childOutPrice;
	}
	
	@Column(name="child_settle_price")
	public BigDecimal getChildSettlePrice() {
		return childSettlePrice;
	}
	public void setChildSettlePrice(BigDecimal childSettlePrice) {
		this.childSettlePrice = childSettlePrice;
	}
	
	@Column(name="student_list_price")
	public BigDecimal getStudentListPrice() {
		return studentListPrice;
	}
	public void setStudentListPrice(BigDecimal studentListPrice) {
		this.studentListPrice = studentListPrice;
	}
	
	@Column(name="student_outsale_price")
	public BigDecimal getStudentOutsalePrice() {
		return studentOutsalePrice;
	}
	public void setStudentOutsalePrice(BigDecimal studentOutsalePrice) {
		this.studentOutsalePrice = studentOutsalePrice;
	}
	
	@Column(name="student_settle_price")
	public BigDecimal getStudentSettlePrice() {
		return studentSettlePrice;
	}
	public void setStudentSettlePrice(BigDecimal studentSettlePrice) {
		this.studentSettlePrice = studentSettlePrice;
	}
	
	@Column(name="old_list_price")
	public BigDecimal getOldListPrice() {
		return oldListPrice;
	}
	public void setOldListPrice(BigDecimal oldListPrice) {
		this.oldListPrice = oldListPrice;
	}
	
	@Column(name="old_outsale_price")
	public BigDecimal getOldOutsalePrice() {
		return oldOutsalePrice;
	}
	public void setOldOutsalePrice(BigDecimal oldOutsalePrice) {
		this.oldOutsalePrice = oldOutsalePrice;
	}
	
	@Column(name="old_settle_price")
	public BigDecimal getOldSettlePrice() {
		return oldSettlePrice;
	}
	public void setOldSettlePrice(BigDecimal oldSettlePrice) {
		this.oldSettlePrice = oldSettlePrice;
	}
	
	@Column(name="inventory")
	public Integer getInventory() {
		return inventory;
	}
	public void setInventory(Integer inventory) {
		this.inventory = inventory;
	}
}
