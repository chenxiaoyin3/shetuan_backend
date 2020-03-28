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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
						})
@Table(name = "hy_ticket_hotelandscene_room")
public class HyTicketHotelandsceneRoom implements Serializable {
    private Long id;
    private HyTicketHotelandscene hyTicketHotelandscene;
    private Integer roomType; //1-大床房,2-标准间,3-双床房
    private Boolean isWifi; //true-可上网,false-不可上网
    private Boolean isWindow; //true-有窗,false-无窗
    private Boolean isBathroom; //true-可沐浴,false-不可沐浴
	private Integer available; //可住人数
    private Integer breakfast; //1-无早餐,2-1人早餐,3-两人早餐
    private Integer saleStatus; //1-未上架,2-已上架,3-已下架
    private Integer auditStatus; //1-未提交,2-已提交,审核中,3-审核通过,4-驳回
    private String processInstanceId;
    private Boolean status; //true-正常,false-取消
    private HyAdmin submitter;
    private Date submitTime;
    /** 房间价格管理*/
    private Set<HyTicketPriceInbound> hyTicketPriceInbounds=new HashSet<>();
    
    //以下字段为门户用
    private Integer mhIsSale; //门户是否上线,0否,1是
    
    
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="hotelandscene")
	public HyTicketHotelandscene getHyTicketHotelandscene() {
		return hyTicketHotelandscene;
	}
	public void setHyTicketHotelandscene(HyTicketHotelandscene hyTicketHotelandscene) {
		this.hyTicketHotelandscene = hyTicketHotelandscene;
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
	
	@Column(name="sale_status")
	public Integer getSaleStatus() {
		return saleStatus;
	}
	public void setSaleStatus(Integer saleStatus) {
		this.saleStatus = saleStatus;
	}
	
	@Column(name="audit_status")
	public Integer getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
	}
	
	@Column(name="process_instance_id")
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	@Column(name="status")
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
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
	
	@JsonProperty
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hyTicketHotelandsceneRoom")
	public Set<HyTicketPriceInbound> getHyTicketPriceInbounds() {
		return hyTicketPriceInbounds;
	}
	public void setHyTicketPriceInbounds(Set<HyTicketPriceInbound> hyTicketPriceInbounds) {
		this.hyTicketPriceInbounds = hyTicketPriceInbounds;
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
