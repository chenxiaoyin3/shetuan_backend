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
@Table(name = "hy_ticket_scene")
public class HyTicketScene implements Serializable {
    private Long id;
    private HySupplier ticketSupplier;
    private Date createTime;
    private Date modifyTime;
    private HyAdmin creator;
    private String sceneName;
    private HyArea area;
    private String sceneAddress;
    private Integer star;
    private Date openTime;
    private Date closeTime;
    private String ticketExchangeAddress;
    private HySupplierElement hySupplierElement;
    private String introduction; ////产品介绍,富文本;用逗号分开产品文件url
    private String ticketFile; //票务推广文件
    private String pn;   
	/** 景区门票管理*/
    private Set<HyTicketSceneTicketManagement> hyTicketSceneTickets=new HashSet<>();
    
    //以下为门户用的相关信息
    private Integer mhState; //门户完善状态,0-未完善，1-已完善，2-供应商有修改,待完善
    private String mhReserveReq; //门户用预订须知
    private String mhSceneName; //门户用景区名称
    private String mhSceneAddress; //门户用景区地址
    private String mhBriefIntroduction; //门户用简要说明
    private String mhIntroduction; //门户用景区介绍,附文本
    private Date mhCreateTime; //门户完善时间
    private Date mhUpdateTime; //门户最后修改时间
    private String mhOperator; //门户完善人
    
    
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
	
	@Column(name="scene_name")
	public String getSceneName() {
		return sceneName;
	}
	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="area")
	public HyArea getArea() {
		return area;
	}
	public void setArea(HyArea area) {
		this.area = area;
	}
	
	@Column(name="scene_address")
	public String getSceneAddress() {
		return sceneAddress;
	}
	public void setSceneAddress(String sceneAddress) {
		this.sceneAddress = sceneAddress;
	}
	
	@Column(name="star")
	public Integer getStar() {
		return star;
	}
	public void setStar(Integer star) {
		this.star = star;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="open_time", length=19)
	@DateTimeFormat(pattern="HH:mm:ss")
	public Date getOpenTime() {
		return openTime;
	}
	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="close_time", length=19)
	@DateTimeFormat(pattern="HH:mm:ss")
	public Date getCloseTime() {
		return closeTime;
	}
	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
	}
	
	@Column(name="ticket_exchange_address")
	public String getTicketExchangeAddress() {
		return ticketExchangeAddress;
	}
	public void setTicketExchangeAddress(String ticketExchangeAddress) {
		this.ticketExchangeAddress = ticketExchangeAddress;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="supplier_element")
	public HySupplierElement getHySupplierElement() {
		return hySupplierElement;
	}
	public void setHySupplierElement(HySupplierElement hySupplierElement) {
		this.hySupplierElement = hySupplierElement;
	}
	
	@Column(name="introduction")
	public String getIntroduction() {
		return introduction;
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
	
	@JsonProperty
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hyTicketScene")
	public Set<HyTicketSceneTicketManagement> getHyTicketSceneTickets() {
		return hyTicketSceneTickets;
	}
	public void setHyTicketSceneTickets(Set<HyTicketSceneTicketManagement> hyTicketSceneTickets) {
		this.hyTicketSceneTickets = hyTicketSceneTickets;
	}
	
	@Column(name="mh_state")
	public Integer getMhState() {
		return mhState;
	}
	public void setMhState(Integer mhState) {
		this.mhState = mhState;
	}
	
	@Column(name="mh_reserve_req")
	public String getMhReserveReq() {
		return mhReserveReq;
	}
	public void setMhReserveReq(String mhReserveReq) {
		this.mhReserveReq = mhReserveReq;
	}
	
	@Column(name="mh_scene_name")
	public String getMhSceneName() {
		return mhSceneName;
	}
	public void setMhSceneName(String mhSceneName) {
		this.mhSceneName = mhSceneName;
	}
	
	@Column(name="mh_scene_address")
	public String getMhSceneAddress() {
		return mhSceneAddress;
	}
	public void setMhSceneAddress(String mhSceneAddress) {
		this.mhSceneAddress = mhSceneAddress;
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
	
	@Column(name="mh_operator")
	public String getMhOperator() {
		return mhOperator;
	}
	public void setMhOperator(String mhOperator) {
		this.mhOperator = mhOperator;
	}
	
	@PrePersist
	public void prePersist(){
		this.setMhState(0); 
	}
}
