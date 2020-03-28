package com.hongyu.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "hy_ticket_hotel_room")
public class HyTicketHotelRoom implements Serializable {
    private Long id;
    private HyTicketHotel hyTicketHotel;
    private String productId;
    private String productName;
    private Integer auditStatus; //1-未提交,2-已提交,审核中,3-审核通过,4-驳回
    private Integer saleStatus; //1-未上架,2-已上架,3-已下架
    private Integer roomType; //1-大床房,2-标准间,3-双床房
    private Boolean isWifi; //true-可上网,false-不可上网
    private Boolean isWindow; //true-有窗,false-无窗
    private Boolean isBathroom; //true-可沐浴,false-不可沐浴
    private Integer available; //可住人数
    private Integer breakfast; //1-无早餐,2-1人早餐,3-两人早餐
    private Integer reserveDays; //提前预订天数
    private Integer reserveTime; //提前预定时间
    private Integer productType; //订单管理处用
    private Boolean status; //true-正常,false-取消
    private String processInstanceId;
    private HyAdmin submitter;
    private Date submitTime;
    private HyPromotionActivity promotionActivity;   
    /** 酒店房间价格管理*/
    private Set<HyTicketPriceInbound> hyTicketPriceInbounds=new HashSet<>();
    
    /**以下是门户用相关字段*/
    private String mhProductName; //门户用产品名称
    private Integer mhIsSale; //门户是否上线,0否,1是

   
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="hotel")
	public HyTicketHotel getHyTicketHotel() {
		return hyTicketHotel;
	}
	public void setHyTicketHotel(HyTicketHotel hyTicketHotel) {
		this.hyTicketHotel = hyTicketHotel;
	}
	
	@Column(name="product_id")
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	@Column(name="product_name")
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	@Column(name="audit_status")
	public Integer getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
	}
	
	@Column(name="sale_status")
	public Integer getSaleStatus() {
		return saleStatus;
	}
	public void setSaleStatus(Integer saleStatus) {
		this.saleStatus = saleStatus;
	}
	
	@Column(name="room_type")
	public Integer getRoomType() {
		return roomType;
	}
	public void setRoomType(Integer roomType) {
		this.roomType = roomType;
	}
	
	@Column(name="is_wifi")
	public Boolean getIsWifi() {
		return isWifi;
	}
	public void setIsWifi(Boolean isWifi) {
		this.isWifi = isWifi;
	}
	
	@Column(name="is_window")
	public Boolean getIsWindow() {
		return isWindow;
	}
	public void setIsWindow(Boolean isWindow) {
		this.isWindow = isWindow;
	}
	
	@Column(name="is_bathroom")
	public Boolean getIsBathroom() {
		return isBathroom;
	}
	public void setIsBathroom(Boolean isBathroom) {
		this.isBathroom = isBathroom;
	}
	
	@Column(name="available")
	public Integer getAvailable() {
		return available;
	}
	public void setAvailable(Integer available) {
		this.available = available;
	}
	
	@Column(name="breakfast")
	public Integer getBreakfast() {
		return breakfast;
	}
	public void setBreakfast(Integer breakfast) {
		this.breakfast = breakfast;
	}
	
	@Column(name="reserve_days")
	public Integer getReserveDays() {
		return reserveDays;
	}
	public void setReserveDays(Integer reserveDays) {
		this.reserveDays = reserveDays;
	}
	
	@Column(name="reserve_time")
	public Integer getReserveTime() {
		return reserveTime;
	}
	public void setReserveTime(Integer reserveTime) {
		this.reserveTime = reserveTime;
	}
	
	@Column(name="product_type")
	 public Integer getProductType() {
		return productType;
	}
	public void setProductType(Integer productType) {
		this.productType = productType;
	}
		
	@Column(name="status")
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
		
	@Column(name="process_instance_id")
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="submitter")
	public HyAdmin getSubmitter() {
		return submitter;
	}
	public void setSubmitter(HyAdmin submitter) {
		this.submitter = submitter;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "submit_time", length = 19)
	@DateTimeFormat(iso=ISO.DATE_TIME)
	public Date getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}
    
    @JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hyTicketHotelRoom")
	public Set<HyTicketPriceInbound> getHyTicketPriceInbounds() {
		return hyTicketPriceInbounds;
	}
    @JsonProperty
	public void setHyTicketPriceInbounds(Set<HyTicketPriceInbound> hyTicketPriceInbounds) {
		this.hyTicketPriceInbounds = hyTicketPriceInbounds;
	}
    
    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name="promotion_id")
	public HyPromotionActivity getPromotionActivity() {
		return promotionActivity;
	}
	public void setPromotionActivity(HyPromotionActivity promotionActivity) {
		this.promotionActivity = promotionActivity;
	}
	
	@Column(name="mh_product_name")
	public String getMhProductName() {
		return mhProductName;
	}
	public void setMhProductName(String mhProductName) {
		this.mhProductName = mhProductName;
	}
	
	@Column(name="mh_is_sale")
	public Integer getMhIsSale() {
		return mhIsSale;
	}
	public void setMhIsSale(Integer mhIsSale) {
		this.mhIsSale = mhIsSale;
	}
	
	@PrePersist
	public void prePersist(){
		this.setMhIsSale(0); 
	}
}
