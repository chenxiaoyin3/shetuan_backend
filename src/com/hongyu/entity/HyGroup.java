package com.hongyu.entity;
// Generated 2017-12-24 21:20:19 by Hibernate Tools 3.6.0.Final

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.grain.entity.BaseEntity;
import com.hongyu.entity.HyLine.LineType;
import com.hongyu.util.Constants.AuditStatus;
import com.hongyu.util.Constants.DeductLine;

/**
 * 团Entity --- 注意新增属性的时候如果需要前端传参，需要用@JsonProperty注解
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_group")
public class HyGroup extends BaseEntity {
	
	public enum GroupStateEnum {
		/** 待出团 */
		daichutuan,
		
		/** 行程中 */
		xingchengzhong,
		
		/** 已结束 */
		yijieshu,
		
		/** 消团中 */
		xiaotuanzhong, 
		
		/** 已取消 */
		yiquxiao,
	}

	/** 团所属线路 */
	private HyLine line;
	
	/** 团可见门店 */
	private Store publishStore;
	
	/** 发团日期 */
	private Date startDay;
	
	/** 回团日期 */
	private Date endDay;
	
	/** 是否内部 */
	private Boolean isInner;
	
	/** 团队类型 */
	private Boolean teamType; //0 : 散客   1 : 团队
	
	/** 发布范围 */
	private String publishRange; //总公司、分公司名称、门店名称 根据团客散客和产品的科建分公司外键来决定
	
	/** 是否有电子券 */
	private Boolean isCoupon;
	
	/** 是否使用所有人 */
	private Boolean isEveryone; // 0：仅成人可用  1：使用所有人
	
	/** 电子券金额 */
	private BigDecimal couponMoney;
	
	/** 团总库存 */
	private Integer stock;
	
	/** 原库存 */
	private Integer stockRemain; //删除
	
	/** 报名人数 */
	private Integer signupNumber; //默认为0
	
	/** 占位人数 */
	private Integer occupyNumber; //默认为0
	
	/** 余位 */
	private Integer remainNumber; //删除

	/** 是否显示团期 */
	private Boolean isDisplay; // 默认false,建团审核通过以后为true,变更扣点，消团，甩尾单会变为false
	
	/** 出团状态 */
	private GroupStateEnum groupState;
	
	/** 甩尾单次数 */
	private Integer saleTimes;
	
	/** 是否消过团 */
	private Boolean isCancel; // 0：否 1：是
	
	/** 扣点方式 */
	private DeductLine koudianType;
	
	/** 团课散客扣点时候的百分比 */
	private BigDecimal percentageKoudian;
	
	/** 人头扣点时候扣点金额 */
	private BigDecimal personKoudian;
	
	/** 是否申请特殊扣点 */
	private Boolean isSpecialKoudian;
	
	/** 团最低价格 */
	private BigDecimal lowestPrice;
	
	/** 是否有成人价 */
	private Boolean isAdult;
	
	/** 是否有儿童价 */
	private Boolean isChild;
	
	/** 是否有学生 */
	private Boolean isStudent;
	
	/** 是否有老人 */
	private Boolean isOld;
	
	/** 是否有单房差 */
	private Boolean isDanfangcha;
	
	/** 是否有补卧铺 */
	private Boolean isBuwopu;
	
	/** 是否有补门票 */
	private Boolean isBumenpiao;
	
	/** 是否有儿童占床 */
	private Boolean isErtongzhanchuang;
	
	/** 是否有补床位 */
	private Boolean isBuchuangwei;
	
	/** 创建人 */
	private HyAdmin creator;
	
	/** 团对应的特殊价格 */
	private List<HyGroupSpecialprice> hyGroupSpecialprices = new ArrayList<>();
	
	/**　团的价格 */
	private List<HyGroupPrice> hyGroupPrices = new ArrayList<>();
	
	/** 团其他价格 */
	private List<HyGroupOtherprice> hyGroupOtherprices = new ArrayList<>();
	
	/** 新增团的时候传过来的开团日期列表 */
	private List<Date> startDays = new ArrayList<>();
	
	/** 团的派单 */
	private Set<GroupSendGuide> groupSendGuides = new HashSet<>();
	
	/** 计调报账 */
	private Long regulateId;
	
	private String applyName; //提交审核人
	
	private AuditStatus auditStatus; //审核状态
	
	private Date applyTime;
	
	private String processInstanceId;
	
	private LineType groupLineType;
	
	private String groupLineName;
	
	private String groupLinePn;
	
	private String operatorName;
	
	private Department groupCompany; //可见分公司
	
	private Boolean isPromotion; //是否在参加促销
	
	//修改电子券逻辑
	private Long couponId; //电子券种类ID --- 指向实体CoupounMoney
	
	private BigDecimal discount; //电子券折扣 --- 直接是折扣数字
	
	//20181122 修改，增加了团的返利类型和返利价格
	private Integer fanliType; // 0:无 1:正价 2:特价 3:自定义
	
	//20190109修改，增加团是否完善// 0:未完善，1：已完善
	private Integer mhState;
	
	@Digits(integer=20, fraction=2)
	private BigDecimal fanliMoney;
	
	
    @JsonProperty
	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinColumn(name = "line")
	public HyLine getLine() {
		return line;
	}

	public void setLine(HyLine line) {
		this.line = line;
	}

	@JsonProperty
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "publish_store")
	public Store getPublishStore() {
		return publishStore;
	}

	public void setPublishStore(Store publishStore) {
		this.publishStore = publishStore;
	}

	@JsonProperty
	@DateTimeFormat(iso=ISO.DATE_TIME)
	@Temporal(TemporalType.DATE)
	@Column(name = "start_day", length = 19)
	public Date getStartDay() {
		return startDay;
	}

	public void setStartDay(Date startDay) {
		this.startDay = startDay;
	}

	@JsonProperty
	@DateTimeFormat(iso=ISO.DATE_TIME)
	@Temporal(TemporalType.DATE)
	@Column(name = "end_day", length = 19)
	public Date getEndDay() {
		return endDay;
	}

	public void setEndDay(Date endDay) {
		this.endDay = endDay;
	}

	@JsonProperty
	public Boolean getIsInner() {
		return isInner;
	}

	public void setIsInner(Boolean isInner) {
		this.isInner = isInner;
	}

	@JsonProperty
	public Boolean getTeamType() {
		return teamType;
	}

	public void setTeamType(Boolean teamType) {
		this.teamType = teamType;
	}

	@JsonProperty
	public String getPublishRange() {
		return publishRange;
	}

	public void setPublishRange(String publishRange) {
		this.publishRange = publishRange;
	}

	@JsonProperty
	public Boolean getIsCoupon() {
		return isCoupon;
	}

	public void setIsCoupon(Boolean isCoupon) {
		this.isCoupon = isCoupon;
	}

	@JsonProperty
	public Boolean getIsEveryone() {
		return isEveryone;
	}

	public void setIsEveryone(Boolean isEveryone) {
		this.isEveryone = isEveryone;
	}

	@JsonProperty
	@Column(name = "coupon_money", precision = 20, scale = 2)
	public BigDecimal getCouponMoney() {
		return couponMoney;
	}

	public void setCouponMoney(BigDecimal couponMoney) {
		this.couponMoney = couponMoney;
	}

	@JsonProperty
	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	@JsonProperty
	public Integer getStockRemain() {
		return stockRemain;
	}

	public void setStockRemain(Integer stockRemain) {
		this.stockRemain = stockRemain;
	}

	@JsonProperty
	public Integer getSignupNumber() {
		return signupNumber;
	}

	public void setSignupNumber(Integer signupNumber) {
		this.signupNumber = signupNumber;
	}

	@JsonProperty
	public Integer getOccupyNumber() {
		return occupyNumber;
	}

	public void setOccupyNumber(Integer occupyNumber) {
		this.occupyNumber = occupyNumber;
	}

	@JsonProperty
	public Integer getRemainNumber() {
		return remainNumber;
	}

	public void setRemainNumber(Integer remainNumber) {
		this.remainNumber = remainNumber;
	}

	@JsonProperty
	public Boolean getIsDisplay() {
		return isDisplay;
	}

	public void setIsDisplay(Boolean isDisplay) {
		this.isDisplay = isDisplay;
	}

	@JsonProperty
	public GroupStateEnum getGroupState() {
		return groupState;
	}

	public void setGroupState(GroupStateEnum groupState) {
		this.groupState = groupState;
	}

	@JsonProperty
	public Integer getSaleTimes() {
		return saleTimes;
	}

	public void setSaleTimes(Integer saleTimes) {
		this.saleTimes = saleTimes;
	}

	@JsonProperty
	public Boolean getIsCancel() {
		return isCancel;
	}

	public void setIsCancel(Boolean isCancel) {
		this.isCancel = isCancel;
	}

	@JsonProperty
	public DeductLine getKoudianType() {
		return koudianType;
	}

	public void setKoudianType(DeductLine koudianType) {
		this.koudianType = koudianType;
	}

	@JsonProperty
	@Column(name = "percentage_koudian", precision = 20, scale = 2)
	public BigDecimal getPercentageKoudian() {
		return percentageKoudian;
	}

	public void setPercentageKoudian(BigDecimal percentageKoudian) {
		this.percentageKoudian = percentageKoudian;
	}

	@JsonProperty
	@Column(name = "person_koudian", precision = 20, scale = 2)
	public BigDecimal getPersonKoudian() {
		return personKoudian;
	}

	public void setPersonKoudian(BigDecimal personKoudian) {
		this.personKoudian = personKoudian;
	}

	@JsonProperty
	public Boolean getIsSpecialKoudian() {
		return isSpecialKoudian;
	}

	public void setIsSpecialKoudian(Boolean isSpecialKoudian) {
		this.isSpecialKoudian = isSpecialKoudian;
	}

	@JsonProperty
	@Column(name = "lowest_price", precision = 20, scale = 2)
	public BigDecimal getLowestPrice() {
		return lowestPrice;
	}

	public void setLowestPrice(BigDecimal lowestPrice) {
		this.lowestPrice = lowestPrice;
	}

	@JsonProperty
	public Boolean getIsAdult() {
		return isAdult;
	}

	public void setIsAdult(Boolean isAdult) {
		this.isAdult = isAdult;
	}

	@JsonProperty
	public Boolean getIsChild() {
		return isChild;
	}

	public void setIsChild(Boolean isChild) {
		this.isChild = isChild;
	}

	@JsonProperty
	public Boolean getIsStudent() {
		return isStudent;
	}

	public void setIsStudent(Boolean isStudent) {
		this.isStudent = isStudent;
	}

	@JsonProperty
	public Boolean getIsOld() {
		return isOld;
	}

	public void setIsOld(Boolean isOld) {
		this.isOld = isOld;
	}
	
	@JsonProperty
	@OrderBy("id asc")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hyGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<HyGroupSpecialprice> getHyGroupSpecialprices() {
		return hyGroupSpecialprices;
	}

	public void setHyGroupSpecialprices(List<HyGroupSpecialprice> hyGroupSpecialprices) {
		this.hyGroupSpecialprices = hyGroupSpecialprices;
	}

	@JsonProperty
	@OrderBy("id asc")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hyGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<HyGroupPrice> getHyGroupPrices() {
		return hyGroupPrices;
	}

	public void setHyGroupPrices(List<HyGroupPrice> hyGroupPrices) {
		this.hyGroupPrices = hyGroupPrices;
	}

	@JsonProperty
	@OrderBy("id asc")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hyGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<HyGroupOtherprice> getHyGroupOtherprices() {
		return hyGroupOtherprices;
	}

	public void setHyGroupOtherprices(List<HyGroupOtherprice> hyGroupOtherprices) {
		this.hyGroupOtherprices = hyGroupOtherprices;
	}

	@JsonProperty
	public Boolean getIsDanfangcha() {
		return isDanfangcha;
	}

	public void setIsDanfangcha(Boolean isDanfangcha) {
		this.isDanfangcha = isDanfangcha;
	}

	@JsonProperty
	public Boolean getIsBuwopu() {
		return isBuwopu;
	}

	public void setIsBuwopu(Boolean isBuwopu) {
		this.isBuwopu = isBuwopu;
	}

	@JsonProperty
	public Boolean getIsBumenpiao() {
		return isBumenpiao;
	}

	public void setIsBumenpiao(Boolean isBumenpiao) {
		this.isBumenpiao = isBumenpiao;
	}

	@JsonProperty
	public Boolean getIsErtongzhanchuang() {
		return isErtongzhanchuang;
	}

	public void setIsErtongzhanchuang(Boolean isErtongzhanchuang) {
		this.isErtongzhanchuang = isErtongzhanchuang;
	}

	@JsonProperty
	public Boolean getIsBuchuangwei() {
		return isBuchuangwei;
	}

	public void setIsBuchuangwei(Boolean isBuchuangwei) {
		this.isBuchuangwei = isBuchuangwei;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator")
	public HyAdmin getCreator() {
		return creator;
	}

	public void setCreator(HyAdmin creator) {
		this.creator = creator;
	}

	@Transient
	@JsonProperty
	public List<Date> getStartDays() {
		return startDays;
	}

	public void setStartDays(List<Date> startDays) {
		this.startDays = startDays;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
	public Set<GroupSendGuide> getGroupSendGuides() {
		return groupSendGuides;
	}

	public void setGroupSendGuides(Set<GroupSendGuide> groupSendGuides) {
		this.groupSendGuides = groupSendGuides;
	}

	public String getApplyName() {
		return applyName;
	}

	public void setApplyName(String applyName) {
		this.applyName = applyName;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "apply_time", length = 19)
	public Date getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	@JsonProperty
	public AuditStatus getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(AuditStatus auditStatus) {
		this.auditStatus = auditStatus;
	}

	public Long getRegulateId() {
		return regulateId;
	}

	public void setRegulateId(Long regulateId) {
		this.regulateId = regulateId;
	}

	public LineType getGroupLineType() {
		return groupLineType;
	}

	public void setGroupLineType(LineType groupLineType) {
		this.groupLineType = groupLineType;
	}

	public String getGroupLineName() {
		return groupLineName;
	}

	public void setGroupLineName(String groupLineName) {
		this.groupLineName = groupLineName;
	}

	public String getGroupLinePn() {
		return groupLinePn;
	}

	public void setGroupLinePn(String groupLinePn) {
		this.groupLinePn = groupLinePn;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company")
	public Department getGroupCompany() {
		return groupCompany;
	}

	public void setGroupCompany(Department groupCompany) {
		this.groupCompany = groupCompany;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public Boolean getIsPromotion() {
		return isPromotion;
	}

	public void setIsPromotion(Boolean isPromotion) {
		this.isPromotion = isPromotion;
	}

	@JsonProperty
	public Long getCouponId() {
		return couponId;
	}

	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}

	@JsonProperty
	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	@JsonProperty
	public Integer getFanliType() {
		return fanliType;
	}

	public void setFanliType(Integer fanliType) {
		this.fanliType = fanliType;
	}

	@JsonProperty
	public BigDecimal getFanliMoney() {
		return fanliMoney;
	}

	public void setFanliMoney(BigDecimal fanliMoney) {
		this.fanliMoney = fanliMoney;
	}
	@JsonProperty
	@Column(name = "mh_state")
	public Integer getMhState() {
		return mhState;
	}

	public void setMhState(Integer mhState) {
		this.mhState = mhState;
	}
}
