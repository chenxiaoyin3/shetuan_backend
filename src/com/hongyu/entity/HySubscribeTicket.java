package com.hongyu.entity;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ManyToAny;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hongyu.util.Constants.AuditStatus;

/**
 * HySubscribeTicket 门店认购门票
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_subscribe_ticket")
public class HySubscribeTicket implements java.io.Serializable {

	public enum SaleStatus {
		weishangjia, yixiajia, yishangjia,
	}

	private Long id;
	private HyAdmin creater;
	private HySupplier supplier;
	private String sn;
	private String sceneName; // 景区名称
	private Date createTime;
	private Date modifyTime;
	private Date applyTime;
	private HyArea area; // 区域
	private String sceneAddress; // 景区地址
	private Integer star; // 星级
	private Date openTime;
	private Date closeTime;
	private String ticketExchangeAddress; // 换票地址
	private HyArea restrictArea; // 限购区域
	private Integer minimum; // 最少购买量
	private Integer days; // 预约天数
	private Date time; // 预约时间
	private String refundRequeirement; // 退票要求
	private String reserveRequirement; // 预约须知
	private AuditStatus auditStatus; // 审核状态--AuditS
	private SaleStatus saleStatus; // 上架状态
	private Set<HySubscribeTicketPrice> hySubscribeTicketPrices = new HashSet<HySubscribeTicketPrice>();

	private String processInstanceId;
	private Boolean status;
	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "creater")
	public HyAdmin getCreater() {
		return creater;
	}

	public void setCreater(HyAdmin creater) {
		this.creater = creater;
	}

	@Column(name = "sn")
	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	@Column(name = "scene_name")
	public String getSceneName() {
		return sceneName;
	}

	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modify_time")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "apply_time")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	public Date getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "area")
	public HyArea getArea() {
		return area;
	}

	public void setArea(HyArea area) {
		this.area = area;
	}

	@Column(name = "scene_address")
	public String getSceneAddress() {
		return sceneAddress;
	}

	public void setSceneAddress(String sceneAddress) {
		this.sceneAddress = sceneAddress;
	}

	@Column(name = "star")
	public Integer getStar() {
		return star;
	}

	public void setStar(Integer star) {
		this.star = star;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "open_time")
	@DateTimeFormat(pattern="HH:mm:ss")
	public Date getOpenTime() {
		return this.openTime;
	}

	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "close_time")
	@DateTimeFormat(pattern="HH:mm:ss")
	public Date getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
	}

	@Column(name = "ticket_exchange_address")
	public String getTicketExchangeAddress() {
		return ticketExchangeAddress;
	}

	public void setTicketExchangeAddress(String ticketExchangeAddress) {
		this.ticketExchangeAddress = ticketExchangeAddress;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "restrict_area")
	public HyArea getRestrictArea() {
		return restrictArea;
	}

	public void setRestrictArea(HyArea restrictArea) {
		this.restrictArea = restrictArea;
	}

	@Column(name = "minimum")
	public Integer getMinimum() {
		return minimum;
	}

	public void setMinimum(Integer minimum) {
		this.minimum = minimum;
	}

	@Column(name = "days")
	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "time")
	@DateTimeFormat(pattern="HH:mm:ss")
	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	@Column(name = "refund_requirement")
	public String getRefundRequeirement() {
		return refundRequeirement;
	}

	public void setRefundRequeirement(String refundRequeirement) {
		this.refundRequeirement = refundRequeirement;
	}

	@Column(name = "reserve_requirement")
	public String getReserveRequirement() {
		return reserveRequirement;
	}

	public void setReserveRequirement(String reserveRequirement) {
		this.reserveRequirement = reserveRequirement;
	}

	@Column(name = "audit_status")
	public AuditStatus getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(AuditStatus auditStatus) {
		this.auditStatus = auditStatus;
	}

	@Column(name = "sale_status")
	public SaleStatus getSaleStatus() {
		return saleStatus;
	}

	public void setSaleStatus(SaleStatus saleStatus) {
		this.saleStatus = saleStatus;
	}

	@JsonProperty
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hySubscribeTicket")
	public Set<HySubscribeTicketPrice> getHySubscribeTicketPrices() {
		return this.hySubscribeTicketPrices;
	}

	public void setHySubscribeTicketPrices(Set<HySubscribeTicketPrice> hySubscribeTicketPrices) {
		this.hySubscribeTicketPrices = hySubscribeTicketPrices;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supplier")
	public HySupplier getSupplier() {
		return supplier;
	}

	public void setSupplier(HySupplier supplier) {
		this.supplier = supplier;
	}

	@Column(name = "process_instance_id")
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
}
