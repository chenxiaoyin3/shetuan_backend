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
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
						})
@Table(name = "hy_ticket_price_inbound")
public class HyTicketPriceInbound implements Serializable {
    private Long id;
    private Date startDate;
    private Date endDate;
    private BigDecimal displayPrice; //挂牌价
    private BigDecimal sellPrice; //外卖价
    private BigDecimal settlementPrice; //结算价
    private Integer inventory; //日库存
    private Integer auditStatus;   
    private HyTicketSceneTicketManagement hyTicketSceneTicketManagement;    
    private HyTicketHotelRoom hyTicketHotelRoom;
    private HyTicketHotelandsceneRoom hyTicketHotelandsceneRoom;
    
    /**以下为门户所用价格字段*/
    private BigDecimal mhDisplayPrice; //门户挂牌价
    private BigDecimal mhSellPrice; //门户外卖价
    private BigDecimal mhPrice; //官网销售价
    
    
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_date", length=19)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_date", length=19)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@Column(name="display_price")
	public BigDecimal getDisplayPrice() {
		return displayPrice;
	}
	public void setDisplayPrice(BigDecimal displayPrice) {
		this.displayPrice = displayPrice;
	}
	
	@Column(name="sell_price")
	public BigDecimal getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}
	
	@Column(name="settlement_price")
	public BigDecimal getSettlementPrice() {
		return settlementPrice;
	}
	public void setSettlementPrice(BigDecimal settlementPrice) {
		this.settlementPrice = settlementPrice;
	}
	
	@Column(name="inventory")
	public Integer getInventory() {
		return inventory;
	}
	public void setInventory(Integer inventory) {
		this.inventory = inventory;
	}
	
	@Column(name="audit_status")
	public Integer getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
	}
	

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ticket_id")
	public HyTicketSceneTicketManagement getHyTicketSceneTicketManagement() {
		return hyTicketSceneTicketManagement;
	}
	public void setHyTicketSceneTicketManagement(HyTicketSceneTicketManagement hyTicketSceneTicketManagement) {
		this.hyTicketSceneTicketManagement = hyTicketSceneTicketManagement;
	}
    
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="room_id")
    public HyTicketHotelRoom getHyTicketHotelRoom() {
		return hyTicketHotelRoom;
	}
	public void setHyTicketHotelRoom(HyTicketHotelRoom hyTicketHotelRoom) {
		this.hyTicketHotelRoom = hyTicketHotelRoom;
	}
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="hotelandscene_room_id")
	public HyTicketHotelandsceneRoom getHyTicketHotelandsceneRoom() {
		return hyTicketHotelandsceneRoom;
	}
	public void setHyTicketHotelandsceneRoom(HyTicketHotelandsceneRoom hyTicketHotelandsceneRoom) {
		this.hyTicketHotelandsceneRoom = hyTicketHotelandsceneRoom;
	}
	
	@Column(name="mh_display_price")
	public BigDecimal getMhDisplayPrice() {
		return mhDisplayPrice;
	}
	public void setMhDisplayPrice(BigDecimal mhDisplayPrice) {
		this.mhDisplayPrice = mhDisplayPrice;
	}
	
	@Column(name="mh_sell_price")
	public BigDecimal getMhSellPrice() {
		return mhSellPrice;
	}
	public void setMhSellPrice(BigDecimal mhSellPrice) {
		this.mhSellPrice = mhSellPrice;
	}
	
	@Column(name="mh_price")
	public BigDecimal getMhPrice() {
		return mhPrice;
	}
	public void setMhPrice(BigDecimal mhPrice) {
		this.mhPrice = mhPrice;
	}
}
