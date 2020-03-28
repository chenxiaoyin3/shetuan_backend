package com.hongyu.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
						})
@Table(name = "hy_ticket_subscribe")
public class HyTicketSubscribe implements Serializable {
    private Long id;
    private HyAdmin creator;
    private String productId;   //产品ID
    private String sceneName;
    private Date createTime;
    private Date modifyTime;
    private HyArea area;
    private String sceneAddress;
    private Integer star; //星级
    private Date openTime; //开放时间
    private Date closeTime; //关闭时间
    private String ticketExchangeAddress;   //换票地址
    private HyArea restrictArea;    //限购区域
    private Integer days; //预约天数
	private Integer reserveTime; //预约时间
    private String refundReq;    //退款说明
    private String reserveKnow;  //预定须知
    private Integer minPurchaseQuantity;   //最小认购数量
    private HySupplier ticketSupplier;
    private Integer auditStatus;   //1-未提交,2-已提交,审核中,3-审核通过,4-审核驳回
    private Integer saleStatus;        //1-未上架,2-已上架,3-已下架
    private Boolean status; //true-正常,false-取消
    private String processInstanceId;
    private HyAdmin submitter;
    private Date submitTime;
    private HySupplierElement hySupplierElement;
    private String introduction; //产品介绍,附文本;用逗号分开产品文件url
    private String ticketFile; //票务推广文件
    private HyPromotionActivity hyPromotionActivity; //促销

    
    
	/*门票价格*/
    private List<HyTicketSubscribePrice> hyTicketSubscribePrices=new ArrayList<>();
    
  
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
	@JoinColumn(name="creator")
	public HyAdmin getCreator() {
		return creator;
	}
	public void setCreator(HyAdmin creator) {
		this.creator = creator;
	}
	
	@Column(name="sn")
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	@Column(name="scene_name")
	public String getSceneName() {
		return sceneName;
	}
	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_time")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="modify_time")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
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
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="open_time", length=19)
	@DateTimeFormat(pattern="HH:mm:ss")
	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="close_time", length=19)
	@DateTimeFormat(iso=ISO.TIME)
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
	@JoinColumn(name="restrict_area")
	public HyArea getRestrictArea() {
		return restrictArea;
	}
	public void setRestrictArea(HyArea restrictArea) {
		this.restrictArea = restrictArea;
	}
	
	@Column(name="days")
	public Integer getDays() {
		return days;
	}
	public void setDays(Integer days) {
		this.days = days;
	}
	
	@Column(name="reserve_time")
	public Integer getReserveTime() {
		return reserveTime;
	}
	public void setReserveTime(Integer reserveTime) {
		this.reserveTime = reserveTime;
	}
	
	@Column(name="refund_req")
	public String getRefundReq() {
		return refundReq;
	}
	public void setRefundReq(String refundReq) {
		this.refundReq = refundReq;
	}
	
	@Column(name="reserve_know")
	public String getReserveKnow() {
		return reserveKnow;
	}
	public void setReserveKnow(String reserveKnow) {
		this.reserveKnow = reserveKnow;
	}
	
	@Column(name="min_purchase_quantity")
	public Integer getMinPurchaseQuantity() {
		return minPurchaseQuantity;
	}
	public void setMinPurchaseQuantity(Integer minPurchaseQuantity) {
		this.minPurchaseQuantity = minPurchaseQuantity;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="supplier_id")
	public HySupplier getTicketSupplier() {
		return ticketSupplier;
	}
	public void setTicketSupplier(HySupplier ticketSupplier) {
		this.ticketSupplier = ticketSupplier;
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
	
	@Column(name="status")
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="supplier_element")
	public HySupplierElement getHySupplierElement() {
		return hySupplierElement;
	}
	public void setHySupplierElement(HySupplierElement hySupplierElement) {
		this.hySupplierElement = hySupplierElement;
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
	@Column(name="submit_time")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	public Date getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
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
	
	@JsonProperty
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ticketSubscribe")
    public List<HyTicketSubscribePrice> getHyTicketSubscribePrices() {
		return hyTicketSubscribePrices;
	}
	public void setHyTicketSubscribePrices(List<HyTicketSubscribePrice> hyTicketSubscribePrices) {
		this.hyTicketSubscribePrices = hyTicketSubscribePrices;
	}
	
	@ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	@JoinColumn(name="promotion_id")
	public HyPromotionActivity getHyPromotionActivity() {
		return hyPromotionActivity;
	}
	public void setHyPromotionActivity(HyPromotionActivity hyPromotionActivity) {
		this.hyPromotionActivity = hyPromotionActivity;
	}
	

}
