package com.hongyu.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

/**票务酒店表*/
@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "hy_ticket_hotel")
public class HyTicketHotel implements Serializable {
	
	public static enum RefundTypeEnum {
		/** 全额退款 */
		quane,
		
		/** 阶梯退款 */
		jieti,
	}
	
    private Long id;
    private HySupplier ticketSupplier;	//虹宇登陆系统录入产品代理的供应商
    private Date createTime;
    private Date modifyTime;
    private HyAdmin creator;
    private String hotelName;
    private HyArea area;
    private Integer star;
    private String address;
    private String reserveKnow;	//预定须知
    private String refundReq;	//退款说明
    private HySupplierElement hySupplierElement;	//提供产品的供应商
    private RefundTypeEnum refundType; //退款类型
    private String introduction;//产品介绍,附文本;用逗号分开产品文件url
    private String ticketFile; //票务推广文件
    private String pn; //酒店的产品编  
	/** 酒店房间管理*/
    private Set<HyTicketHotelRoom> hyTicketHotelRooms=new HashSet<>();
    
    /**以下为门户用的相关字段信息*/
    private String mhOperator; //门户完善人
    private Date mhCreateTime; //门户完善时间
    private Date mhUpdateTime; //门户最后修改时间
    private String mhHotelName; //门户用酒店名称
    private String mhAddress; //门户酒店地址
    private String mhReserveKnow; //门户用预订须知
    private String mhRefundReq; //门户用退款说明
    private RefundTypeEnum mhRefundType; //门户用退款类型
    private String mhBriefIntroduction; //门户用简要说明
    private String mhIntroduction; //门户用产品介绍,附文本
    private Integer mhIsHot; //门户用,是否热门酒店,0否,1是
    private Integer mhState; //门户完善状态,0未完善;1已完善;2供应商有修改,未同步完善
    
    
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
	@JoinColumn(name="ticket_supplier")
	public HySupplier getTicketSupplier() {
		return ticketSupplier;
	}
	public void setTicketSupplier(HySupplier ticketSupplier) {
		this.ticketSupplier = ticketSupplier;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", length = 19)
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modify_time", length = 19)
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) { 
		this.modifyTime = modifyTime;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="creator")
	public HyAdmin getCreator() {
		return creator;
	}
	public void setCreator(HyAdmin creator) {
		this.creator = creator;
	}
	
	@Column(name="hotel_name")
	public String getHotelName() {
		return hotelName;
	}
	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="area_id")
	public HyArea getArea() {
		return area;
	}
	public void setArea(HyArea area) {
		this.area = area;
	}
	
	@Column(name="star")
	public Integer getStar() {
		return star;
	}
	public void setStar(Integer star) {
		this.star = star;
	}
	
	@Column(name="address")
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	@Column(name="reserve_know")
	public String getReserveKnow() {
		return reserveKnow;
	}
	public void setReserveKnow(String reserveKnow) {
		this.reserveKnow = reserveKnow;
	}
    
	@Column(name="refund_req")
	public String getRefundReq() {
		return refundReq;
	}
	public void setRefundReq(String refundReq) {
		this.refundReq = refundReq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="supplier_element")
	public HySupplierElement getHySupplierElement() {
		return hySupplierElement;
	}
	public void setHySupplierElement(HySupplierElement hySupplierElement) {
		this.hySupplierElement = hySupplierElement;
	}
	
	@Column(name="refund_type")
	public RefundTypeEnum getRefundType() {
		return refundType;
	}
	public void setRefundType(RefundTypeEnum refundType) {
		this.refundType = refundType;
	}
	
	@JsonProperty
	@Column(name = "introduction")
	public String getIntroduction() {
		return this.introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	
	@Column(name="ticket_file")
	public String getTicketFile() {
		return ticketFile;
	}
	public void setTicketFile(String ticketFile) {
		this.ticketFile = ticketFile;
	}
	
	@Column(name="pn")
	public String getPn() {
		return pn;
	}
	public void setPn(String pn) {
		this.pn = pn;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hyTicketHotel")
    @JsonIgnore
	public Set<HyTicketHotelRoom> getHyTicketHotelRooms() {
		return hyTicketHotelRooms;
	}
	@JsonProperty
	public void setHyTicketHotelRooms(Set<HyTicketHotelRoom> hyTicketHotelRooms) {
		this.hyTicketHotelRooms = hyTicketHotelRooms;
	}
	
	@Column(name="mh_operator")
	public String getMhOperator() {
		return mhOperator;
	}
	public void setMhOperator(String mhOperator) {
		this.mhOperator = mhOperator;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "mh_create_time", length = 19)
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	public Date getMhCreateTime() {
		return mhCreateTime;
	}
	public void setMhCreateTime(Date mhCreateTime) {
		this.mhCreateTime = mhCreateTime;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "mh_update_time", length = 19)
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	public Date getMhUpdateTime() {
		return mhUpdateTime;
	}
	public void setMhUpdateTime(Date mhUpdateTime) {
		this.mhUpdateTime = mhUpdateTime;
	}
	
	@Column(name="mh_hotel_name")
	public String getMhHotelName() {
		return mhHotelName;
	}
	public void setMhHotelName(String mhHotelName) {
		this.mhHotelName = mhHotelName;
	}
	
	@Column(name="mh_address")
	public String getMhAddress() {
		return mhAddress;
	}
	public void setMhAddress(String mhAddress) {
		this.mhAddress = mhAddress;
	}
	
	@Column(name="mh_reserve_know")
	public String getMhReserveKnow() {
		return mhReserveKnow;
	}
	public void setMhReserveKnow(String mhReserveKnow) {
		this.mhReserveKnow = mhReserveKnow;
	}
	
	@Column(name="mh_refund_req")
	public String getMhRefundReq() {
		return mhRefundReq;
	}
	public void setMhRefundReq(String mhRefundReq) {
		this.mhRefundReq = mhRefundReq;
	}
	
	@Column(name="mh_refund_type")
	public RefundTypeEnum getMhRefundType() {
		return mhRefundType;
	}
	public void setMhRefundType(RefundTypeEnum mhRefundType) {
		this.mhRefundType = mhRefundType;
	}
	
	@Column(name="mh_brief_introduction")
	public String getMhBriefIntroduction() {
		return mhBriefIntroduction;
	}
	public void setMhBriefIntroduction(String mhBriefIntroduction) {
		this.mhBriefIntroduction = mhBriefIntroduction;
	}
	
	@Column(name="mh_introduction")
	public String getMhIntroduction() {
		return mhIntroduction;
	}
	public void setMhIntroduction(String mhIntroduction) {
		this.mhIntroduction = mhIntroduction;
	}
	
	@Column(name="mh_is_hot")
	public Integer getMhIsHot() {
		return mhIsHot;
	}
	public void setMhIsHot(Integer mhIsHot) {
		this.mhIsHot = mhIsHot;
	}
	
	@Column(name="mh_state")
	public Integer getMhState() {
		return mhState;
	}
	public void setMhState(Integer mhState) {
		this.mhState = mhState;
	}
	
	@PrePersist
	public void prePersist(){
		this.setMhState(0); 
	}
}
