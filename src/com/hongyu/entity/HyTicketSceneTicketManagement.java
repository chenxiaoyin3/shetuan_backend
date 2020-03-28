package com.hongyu.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
						})
@Table(name = "hy_ticket_scene_ticket_management")
public class HyTicketSceneTicketManagement implements Serializable {
    private Long id;
    private String productId; //程序自动生成
    private String productName;
    private HyTicketScene hyTicketScene;
    private Integer auditStatus; //1-未提交,2-已提交,审核中,3-审核通过,4-驳回
    private Integer saleStatus; //1-未上架,2-已上架,3-已下架
    private Integer ticketType; //1-成人票,2-学生票,3-儿童票,4-老人票
    private Boolean isReserve; //是否预定,true-预定,false_不预定
    private Integer days;
    private Integer times;
    private Boolean isRealName; //是否实名,true-实名,false-不实名
    private String refundReq; //退款说明
    private String realNameRemark; //实名说明
    private String reserveReq; //预定须知
    private Integer productType; //票务管理处用,暂时无用
    private Boolean status; //true-正常,false-取消
    private String processInstanceId;
    private HyAdmin operator;
    private Date submitTime;
    private HyPromotionActivity hyPromotionActivity; //促销
    /** 门票价格库存管理*/
    private Set<HyTicketPriceInbound> hyTicketPriceInbounds=new HashSet<>();
    
    //以下字段为门户用
    private String mhProductName; //门户用产品名称
    private String mhReserveReq; //门户预订须知
    private String mhRefundReq; //门户退款说明
    private Integer mhIsSale; //门户上线状态,0-下线,1-上线
      
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	
	@JsonProperty
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="scene_id")
	public HyTicketScene getHyTicketScene() {
		return hyTicketScene;
	}
	public void setHyTicketScene(HyTicketScene hyTicketScene) {
		this.hyTicketScene = hyTicketScene;
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
	
	@Column(name="ticket_type")
	public Integer getTicketType() {
		return ticketType;
	}
	public void setTicketType(Integer ticketType) {
		this.ticketType = ticketType;
	}
	
	@Column(name="is_reserve")
	public Boolean getIsReserve() {
		return isReserve;
	}
	public void setIsReserve(Boolean isReserve) {
		this.isReserve = isReserve;
	}
	
	@Column(name="days")
	public Integer getDays() {
		return days;
	}
	public void setDays(Integer days) {
		this.days = days;
	}
	
	@Column(name="times")
	public Integer getTimes() {
		return times;
	}
	public void setTimes(Integer times) {
		this.times = times;
	}
	
	@Column(name="is_real_name")
	public Boolean getIsRealName() {
		return isRealName;
	}
	public void setIsRealName(Boolean isRealName) {
		this.isRealName = isRealName;
	}
	
	@Column(name="refund_req")
	public String getRefundReq() {
		return refundReq;
	}
	public void setRefundReq(String refundReq) {
		this.refundReq = refundReq;
	}
	
	@Column(name="real_name_remark")
	public String getRealNameRemark() {
		return realNameRemark;
	}
	public void setRealNameRemark(String realNameRemark) {
		this.realNameRemark = realNameRemark;
	}
	
	@Column(name="reserve_req")
	public String getReserveReq() {
		return reserveReq;
	}
	public void setReserveReq(String reserveReq) {
		this.reserveReq = reserveReq;
	}
	
	@Column(name="product_type")
	public Integer getProductType() {
		return productType;
	}
	public void setProductType(Integer productType) {
		this.productType = productType;
	}
    
	@JsonProperty
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hyTicketSceneTicketManagement")
	public Set<HyTicketPriceInbound> getHyTicketPriceInbounds() {
	    return hyTicketPriceInbounds;
	}
    public void setHyTicketPriceInbounds(Set<HyTicketPriceInbound> hyTicketPriceInbounds) {
		this.hyTicketPriceInbounds = hyTicketPriceInbounds;
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
	
	@JsonProperty
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="operator")
	public HyAdmin getOperator() {
		return operator;
	}
	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="submit_time", length=19)
	public Date getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}
	
	//promotion_id
    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name="promotion_id")
	public HyPromotionActivity getHyPromotionActivity() {
		return hyPromotionActivity;
	}
	public void setHyPromotionActivity(HyPromotionActivity hyPromotionActivity) {
		this.hyPromotionActivity = hyPromotionActivity;
	}

	@Column(name="mh_product_name")
	public String getMhProductName() {
		return mhProductName;
	}
	public void setMhProductName(String mhProductName) {
		this.mhProductName = mhProductName;
	}
	
	@Column(name="mh_reserve_req")
	public String getMhReserveReq() {
		return mhReserveReq;
	}
	public void setMhReserveReq(String mhReserveReq) {
		this.mhReserveReq = mhReserveReq;
	}
	
	@Column(name="mh_refund_req")
	public String getMhRefundReq() {
		return mhRefundReq;
	}
	public void setMhRefundReq(String mhRefundReq) {
		this.mhRefundReq = mhRefundReq;
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
