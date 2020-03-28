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
 * HySubscribeTicketPrice 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_subscribe_ticket_price")
public class HySubscribeTicketPrice implements java.io.Serializable {

	private Long id;
	private HySubscribeTicket hySubscribeTicket;
	private Date startDate;
	private Date endDate;
	private BigDecimal adultDisplayPrice;
	private BigDecimal adultSellPrice;
	private BigDecimal adultSystemPrice;
	private BigDecimal childDisplayPrice;
	private BigDecimal childSellPrice;
	private BigDecimal childSystemPrice;
	private BigDecimal studentDisplayPrice;
	private BigDecimal studentSellPrice;
	private BigDecimal studentSystemPrice;
	private BigDecimal oldDisplayPrice;
	private BigDecimal oldSellPrice;
	private BigDecimal oldSystemPrice;
	private Integer dayInventory;

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
	@JoinColumn(name = "subscribe_ticket")
	public HySubscribeTicket getHySubscribeTicket() {
		return this.hySubscribeTicket;
	}

	public void setHySubscribeTicket(HySubscribeTicket hySubscribeTicket) {
		this.hySubscribeTicket = hySubscribeTicket;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_date", length = 19)
	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date", length = 19)
	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Column(name = "adult_display_price", precision = 10, scale = 0)
	public BigDecimal getAdultDisplayPrice() {
		return this.adultDisplayPrice;
	}

	public void setAdultDisplayPrice(BigDecimal adultDisplayPrice) {
		this.adultDisplayPrice = adultDisplayPrice;
	}

	@Column(name = "adult_sell_price", precision = 10, scale = 0)
	public BigDecimal getAdultSellPrice() {
		return this.adultSellPrice;
	}

	public void setAdultSellPrice(BigDecimal adultSellPrice) {
		this.adultSellPrice = adultSellPrice;
	}

	@Column(name = "adult_system_price", precision = 10, scale = 0)
	public BigDecimal getAdultSystemPrice() {
		return this.adultSystemPrice;
	}

	public void setAdultSystemPrice(BigDecimal adultSystemPrice) {
		this.adultSystemPrice = adultSystemPrice;
	}

	@Column(name = "child_display_price", precision = 10, scale = 0)
	public BigDecimal getChildDisplayPrice() {
		return this.childDisplayPrice;
	}

	public void setChildDisplayPrice(BigDecimal childDisplayPrice) {
		this.childDisplayPrice = childDisplayPrice;
	}

	@Column(name = "child_sell_price", precision = 10, scale = 0)
	public BigDecimal getChildSellPrice() {
		return this.childSellPrice;
	}

	public void setChildSellPrice(BigDecimal childSellPrice) {
		this.childSellPrice = childSellPrice;
	}

	@Column(name = "child_system_price", precision = 10, scale = 0)
	public BigDecimal getChildSystemPrice() {
		return this.childSystemPrice;
	}

	public void setChildSystemPrice(BigDecimal childSystemPrice) {
		this.childSystemPrice = childSystemPrice;
	}

	@Column(name = "student_display_price", precision = 10, scale = 0)
	public BigDecimal getStudentDisplayPrice() {
		return this.studentDisplayPrice;
	}

	public void setStudentDisplayPrice(BigDecimal studentDisplayPrice) {
		this.studentDisplayPrice = studentDisplayPrice;
	}

	@Column(name = "student_sell_price", precision = 10, scale = 0)
	public BigDecimal getStudentSellPrice() {
		return this.studentSellPrice;
	}

	public void setStudentSellPrice(BigDecimal studentSellPrice) {
		this.studentSellPrice = studentSellPrice;
	}

	@Column(name = "student_system_price", precision = 10, scale = 0)
	public BigDecimal getStudentSystemPrice() {
		return this.studentSystemPrice;
	}

	public void setStudentSystemPrice(BigDecimal studentSystemPrice) {
		this.studentSystemPrice = studentSystemPrice;
	}

	@Column(name = "old_display_price", precision = 10, scale = 0)
	public BigDecimal getOldDisplayPrice() {
		return this.oldDisplayPrice;
	}

	public void setOldDisplayPrice(BigDecimal oldDisplayPrice) {
		this.oldDisplayPrice = oldDisplayPrice;
	}

	@Column(name = "old_sell_price", precision = 10, scale = 0)
	public BigDecimal getOldSellPrice() {
		return this.oldSellPrice;
	}

	public void setOldSellPrice(BigDecimal oldSellPrice) {
		this.oldSellPrice = oldSellPrice;
	}

	@Column(name = "old_system_price", precision = 10, scale = 0)
	public BigDecimal getOldSystemPrice() {
		return this.oldSystemPrice;
	}

	public void setOldSystemPrice(BigDecimal oldSystemPrice) {
		this.oldSystemPrice = oldSystemPrice;
	}

	@Column(name = "day_inventory")
	public Integer getDayInventory() {
		return this.dayInventory;
	}

	public void setDayInventory(Integer dayInventory) {
		this.dayInventory = dayInventory;
	}

}
