package com.hongyu.entity;

import java.math.BigDecimal;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hongyu.util.Constants;

@Entity
@Table(name = "hy_promotion_activity")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
public class HyPromotionActivity {
	private Long id;
	/** 计调  **/
	private HyAdmin jidiao;
	/** 促销名称 **/
	private String name;
	/** 促销开始时间 **/
	private Date startDate;
	/** 促销结束时间 **/
	private Date endDate;
	/** 促销类型  0:满减,1:满折,2:每人(单位数量)减**/
	private Integer promotionType;
	/** 满减促销满足的金额 **/
	private BigDecimal manjianPrice1;
	/** 满减促销减免的金额 **/
	private BigDecimal manjianPrice2;
	/** 打折折扣 **/
	private BigDecimal dazhe;
	/** 每人减/按数量减金额 **/
	private BigDecimal meirenjian;
	/** 审核状态 0:待审核 1:通过 2:驳回  3:已过期 4:已取消**/
	private Integer state;
	/** 申请人 **/
	private HyAdmin applyName;
	/** 申请时间 **/
	private Date applyTime;
	/** 流程实例id **/
	private String processInstanceId;
	/** 备注 **/
	private String remark;
	/** 是否采购提出 **/
	private Boolean isCaigouti;
	/** 审核人 **/
	private HyAdmin auditor;
	/** 审核时间  **/
	private Date auditTime;
	/** 活动类型 0:门票,1:酒店,2:酒+景,3:认购门票,4:签证 **/
	private Integer activityType;
	/** 酒店房型促销 **/
	private Set<HyTicketHotelRoom> rooms = new HashSet<>();
	/** 门票促销 **/
	private Set<HyTicketSceneTicketManagement> ticketScenes = new HashSet<>();
	/** 酒+景促销 **/
	private Set<HyTicketHotelandscene> hotelAndScenes = new HashSet<>();
	/** 认购门票促销  **/
	private Set<HyTicketSubscribe> ticketSubscribes = new HashSet<>();
	/** 签证促销  **/
	private Set<HyVisa> visas = new HashSet<>();
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", unique=true, nullable=false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "jidiao")
	public HyAdmin getJidiao() {
		return jidiao;
	}
	public void setJidiao(HyAdmin jidiao) {
		this.jidiao = jidiao;
	}
	
	@Column(name="name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_date")
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_date")
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@Column(name="promotion_type")
	public Integer getPromotionType() {
		return promotionType;
	}
	public void setPromotionType(Integer promotionType) {
		this.promotionType = promotionType;
	}
	
	@Column(name="manjian_price1", precision=20, scale=2)
	public BigDecimal getManjianPrice1() {
		return manjianPrice1;
	}
	public void setManjianPrice1(BigDecimal manjianPrice1) {
		this.manjianPrice1 = manjianPrice1;
	}
	
	@Column(name="manjian_price2", precision=20, scale=2)
	public BigDecimal getManjianPrice2() {
		return manjianPrice2;
	}
	public void setManjianPrice2(BigDecimal manjianPrice2) {
		this.manjianPrice2 = manjianPrice2;
	}
	
	@Column(name="dazhe", precision=10, scale=4)
	public BigDecimal getDazhe() {
		return dazhe;
	}
	public void setDazhe(BigDecimal dazhe) {
		this.dazhe = dazhe;
	}
	
	@Column(name="meirenjian", precision=20, scale=2)
	public BigDecimal getMeirenjian() {
		return meirenjian;
	}
	public void setMeirenjian(BigDecimal meirenjian) {
		this.meirenjian = meirenjian;
	}
	
	@Column(name="state")
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "apply_name")
	public HyAdmin getApplyName() {
		return applyName;
	}
	public void setApplyName(HyAdmin applyName) {
		this.applyName = applyName;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="apply_time")
	public Date getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}
	
	@Column(name="process_instance_id")
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	@Column(name="remark")
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	@Column(name="is_caigouti")
	public Boolean getIsCaigouti() {
		return isCaigouti;
	}
	public void setIsCaigouti(Boolean isCaigouti) {
		this.isCaigouti = isCaigouti;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "auditor")
	public HyAdmin getAuditor() {
		return auditor;
	}
	public void setAuditor(HyAdmin auditor) {
		this.auditor = auditor;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="audit_time")
	public Date getAuditTime() {
		return auditTime;
	}
	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}
	
	@Column(name="activity_type")
	public Integer getActivityType() {
		return activityType;
	}
	public void setActivityType(Integer activityType) {
		this.activityType = activityType;
	}
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinTable(name = "hy_promotionactivity_tickethotelroom", joinColumns = {
			@JoinColumn(name = "promotion_id", nullable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "room_id", nullable = false) })
	public Set<HyTicketHotelRoom> getRooms() {
		return rooms;
	}
	public void setRooms(Set<HyTicketHotelRoom> rooms) {
		this.rooms = rooms;
	}
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinTable(name = "hy_promotionactivity_ticketscene", joinColumns = {
			@JoinColumn(name = "promotion_id", nullable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "ticketscene_id", nullable = false) })
	public Set<HyTicketSceneTicketManagement> getTicketScenes() {
		return ticketScenes;
	}
	public void setTicketScenes(Set<HyTicketSceneTicketManagement> ticketScenes) {
		this.ticketScenes = ticketScenes;
	}
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinTable(name = "hy_promotionactivity_tickethotelscene", joinColumns = {
			@JoinColumn(name = "promotion_id", nullable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "tickethotelscene_id", nullable = false) })
	public Set<HyTicketHotelandscene> getHotelAndScenes() {
		return hotelAndScenes;
	}
	public void setHotelAndScenes(Set<HyTicketHotelandscene> hotelAndScenes) {
		this.hotelAndScenes = hotelAndScenes;
	}
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinTable(name = "hy_promotionactivity_ticketsubscribe", joinColumns = {
			@JoinColumn(name = "promotion_id", nullable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "ticketsubscribe_id", nullable = false) })
	public Set<HyTicketSubscribe> getTicketSubscribes() {
		return ticketSubscribes;
	}
	public void setTicketSubscribes(Set<HyTicketSubscribe> ticketSubscribes) {
		this.ticketSubscribes = ticketSubscribes;
	}
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinTable(name = "hy_promotionactivity_visa", joinColumns = {
			@JoinColumn(name = "promotion_id", nullable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "visa_id", nullable = false) })
	public Set<HyVisa> getVisas() {
		return visas;
	}
	public void setVisas(Set<HyVisa> visas) {
		this.visas = visas;
	}
	
	@PrePersist
	public void setPrepersist() {
		this.applyTime = new Date();
		this.state = Constants.PROMOTION_ACTIVITY_STATUS_AUDITING;
	}
	
	
}
