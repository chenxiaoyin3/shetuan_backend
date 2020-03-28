package com.hongyu.entity;

import java.io.Serializable;
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
@Table(name = "hy_ticket_hotelandscene")
public class HyTicketHotelandscene implements Serializable {
	
	public enum RefundTypeEnum {
		/** 全额退款 */
		quane,
		
		/** 阶梯退款 */
		jieti,
	}
	
    private Long id;
    private HySupplier ticketSupplier;
    private String productId;
    private String productName;
    private Date createTime;
    private Date modifyTime;
    private HyAdmin creator;
    private Date latestPriceDate;
    private BigDecimal lowestPrice;
    private Boolean isRealName; //true-实名,false-非实名
    private Integer days; //行程天数
    private String priceContain; //费用包含
    private Integer reserveDays;
    private Integer reserveTime;
    private String reserveKnow; //预定须知
    private String refundKnow; //退票说明
    private RefundTypeEnum refundType;
    private String sceneName;
    private HyArea sceneArea;
    private String sceneAddress;
    private Integer sceneStar;
    private Date sceneOpenTime;
    private Date sceneCloseTime;
    private String exchangeTicketAddress; //换票地址
    private Integer adultsTicketNum; //成人票数量
    private Integer childrenTicketNum; //儿童票数量
    private Integer studentsTicketNum; //学生票数量
    private Integer oldTicketNum; //老年票数量
    private String hotelName;
    private Integer hotelStar;
    private HyArea hotelArea;
    private String hotelAddress;
    private HySupplierElement hySupplierElement; //旅游元素供应商
    private String introduction; //产品介绍,附文本;用逗号分开产品文件url
    private String ticketFile; //票务推广文件
    private HyPromotionActivity hyPromotionActivity; //促销
	/*酒加景房间管理*/
    private Set<HyTicketHotelandsceneRoom> hyTicketHotelandsceneRooms=new HashSet<>();
    
    //以下为门户用字段
    private Integer mhState; //门户完善状态,0-未完善,1-已完善,2-供应商有修改,待完善
    private String mhProductName; //门户用产品名称
    private String mhPriceContain; //门户用费用包含
    private String mhReserveKnow; //门户用预订须知
    private String mhRefundKnow; //门户用退款说明
    private RefundTypeEnum mhRefundType; //门户用退款类型
    private String mhSceneName; //门户用景区名称
    private String mhSceneAddress; //门户用景区地址
    private String mhHotelName; //门户用酒店名称
    private String mhHotelAddress; //门户用酒店地址
    private String mhBriefIntroduction; //门户用简要说明
    private String mhIntroduction; //门户用产品介绍,附文本
    private Date mhCreateTime; //门户完善时间
    private Date mhUpdateTime; //最后修改时间
    private String mhOperator; //完善人
    private Integer mhIsHot; //是否热门酒加景,0否,1是
    
    
  
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
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "latest_price_date", length = 19)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getLatestPriceDate() {
		return latestPriceDate;
	}
	public void setLatestPriceDate(Date latestPriceDate) {
		this.latestPriceDate = latestPriceDate;
	}
	
	@Column(name="lowest_price")
	public BigDecimal getLowestPrice() {
		return lowestPrice;
	}
	public void setLowestPrice(BigDecimal lowestPrice) {
		this.lowestPrice = lowestPrice;
	}
	
	@Column(name="is_real_name")
	public Boolean getIsRealName() {
		return isRealName;
	}
	public void setIsRealName(Boolean isRealName) {
		this.isRealName = isRealName;
	}
	
	@Column(name="days")
	public Integer getDays() {
		return days;
	}
	public void setDays(Integer days) {
		this.days = days;
	}
	
	@Column(name="price_contain")
	public String getPriceContain() {
		return priceContain;
	}
	public void setPriceContain(String priceContain) {
		this.priceContain = priceContain;
	}
	
	@Column(name="reserve_days")
	public Integer getReserveDays() {
		return reserveDays;
	}
	public void setReserveDays(Integer reserveDays) {
		this.reserveDays = reserveDays;
	}
	
	@Column(name = "reserve_time")
	public Integer getReserveTime() {
		return reserveTime;
	}
	public void setReserveTime(Integer reserveTime) {
		this.reserveTime = reserveTime;
	}
	
	@Column(name="reserve_know")
	public String getReserveKnow() {
		return reserveKnow;
	}
	public void setReserveKnow(String reserveKnow) {
		this.reserveKnow = reserveKnow;
	}
	
	@Column(name="refund_know")
	public String getRefundKnow() {
		return refundKnow;
	}
	public void setRefundKnow(String refundKnow) {
		this.refundKnow = refundKnow;
	}
	
	@Column(name="scene_name")
	public String getSceneName() {
		return sceneName;
	}
	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="scene_area")
	public HyArea getSceneArea() {
		return sceneArea;
	}
	public void setSceneArea(HyArea sceneArea) {
		this.sceneArea = sceneArea;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="hotel_area")
	public HyArea getHotelArea() {
		return hotelArea;
	}
	public void setHotelArea(HyArea hotelArea) {
		this.hotelArea = hotelArea;
	}
	@Column(name="scene_address")
	public String getSceneAddress() {
		return sceneAddress;
	}
	public void setSceneAddress(String sceneAddress) {
		this.sceneAddress = sceneAddress;
	}
	
	@Column(name="scene_star")
	public Integer getSceneStar() {
		return sceneStar;
	}
	public void setSceneStar(Integer sceneStar) {
		this.sceneStar = sceneStar;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "scene_open_time", length = 19)
	@DateTimeFormat(pattern="HH:mm:ss")
	public Date getSceneOpenTime() {
		return sceneOpenTime;
	}
	public void setSceneOpenTime(Date sceneOpenTime) {
		this.sceneOpenTime = sceneOpenTime;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "scene_close_time", length = 19)
	@DateTimeFormat(pattern="HH:mm:ss")
	public Date getSceneCloseTime() {
		return sceneCloseTime;
	}
	public void setSceneCloseTime(Date sceneCloseTime) {
		this.sceneCloseTime = sceneCloseTime;
	}
	
	@Column(name="exchange_ticket_address")
	public String getExchangeTicketAddress() {
		return exchangeTicketAddress;
	}
	public void setExchangeTicketAddress(String exchangeTicketAddress) {
		this.exchangeTicketAddress = exchangeTicketAddress;
	}
	
	@Column(name="adults_ticket_num")
	public Integer getAdultsTicketNum() {
		return adultsTicketNum;
	}
	public void setAdultsTicketNum(Integer adultsTicketNum) {
		this.adultsTicketNum = adultsTicketNum;
	}
	
	@Column(name="children_ticket_num")
	public Integer getChildrenTicketNum() {
		return childrenTicketNum;
	}
	public void setChildrenTicketNum(Integer childrenTicketNum) {
		this.childrenTicketNum = childrenTicketNum;
	}
	
	@Column(name="students_ticket_num")
	public Integer getStudentsTicketNum() {
		return studentsTicketNum;
	}
	public void setStudentsTicketNum(Integer studentsTicketNum) {
		this.studentsTicketNum = studentsTicketNum;
	}
	
	@Column(name="old_ticket_num")
	public Integer getOldTicketNum() {
		return oldTicketNum;
	}
	public void setOldTicketNum(Integer oldTicketNum) {
		this.oldTicketNum = oldTicketNum;
	}
	
	@Column(name="hotel_name")
	public String getHotelName() {
		return hotelName;
	}
	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}
	
	@Column(name="hotel_star")
	public Integer getHotelStar() {
		return hotelStar;
	}
	public void setHotelStar(Integer hotelStar) {
		this.hotelStar = hotelStar;
	}
	
	@Column(name="hotel_address")
	public String getHotelAddress() {
		return hotelAddress;
	}
	public void setHotelAddress(String hotelAddress) {
		this.hotelAddress = hotelAddress;
	}
	
	@Column(name="refund_type")
	public RefundTypeEnum getRefundType() {
		return refundType;
	}
	public void setRefundType(RefundTypeEnum refundType) {
		this.refundType = refundType;
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
	
	@ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	@JoinColumn(name="promotion_id")
	public HyPromotionActivity getHyPromotionActivity() {
		return hyPromotionActivity;
	}
	public void setHyPromotionActivity(HyPromotionActivity hyPromotionActivity) {
		this.hyPromotionActivity = hyPromotionActivity;
	}
	
	@JsonProperty
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hyTicketHotelandscene")
	public Set<HyTicketHotelandsceneRoom> getHyTicketHotelandsceneRooms() {
		return hyTicketHotelandsceneRooms;
	}
	public void setHyTicketHotelandsceneRooms(Set<HyTicketHotelandsceneRoom> hyTicketHotelandsceneRooms) {
		this.hyTicketHotelandsceneRooms = hyTicketHotelandsceneRooms;
	}
	
	
	@Column(name="mh_state")
	public Integer getMhState() {
		return mhState;
	}
	public void setMhState(Integer mhState) {
		this.mhState = mhState;
	}
	
	@Column(name="mh_product_name")
	public String getMhProductName() {
		return mhProductName;
	}
	public void setMhProductName(String mhProductName) {
		this.mhProductName = mhProductName;
	}
	
	@Column(name="mh_price_contain")
	public String getMhPriceContain() {
		return mhPriceContain;
	}
	public void setMhPriceContain(String mhPriceContain) {
		this.mhPriceContain = mhPriceContain;
	}
	
	@Column(name="mh_reserve_know")
	public String getMhReserveKnow() {
		return mhReserveKnow;
	}
	public void setMhReserveKnow(String mhReserveKnow) {
		this.mhReserveKnow = mhReserveKnow;
	}
	
	@Column(name="mh_refund_know")
	public String getMhRefundKnow() {
		return mhRefundKnow;
	}
	public void setMhRefundKnow(String mhRefundKnow) {
		this.mhRefundKnow = mhRefundKnow;
	}
	
	@Column(name="mh_refund_type")
	public RefundTypeEnum getMhRefundType() {
		return mhRefundType;
	}
	public void setMhRefundType(RefundTypeEnum mhRefundType) {
		this.mhRefundType = mhRefundType;
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
	
	@Column(name="mh_hotel_name")
	public String getMhHotelName() {
		return mhHotelName;
	}
	public void setMhHotelName(String mhHotelName) {
		this.mhHotelName = mhHotelName;
	}
	
	@Column(name="mh_hotel_address")
	public String getMhHotelAddress() {
		return mhHotelAddress;
	}
	public void setMhHotelAddress(String mhHotelAddress) {
		this.mhHotelAddress = mhHotelAddress;
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
	
	@Column(name="mh_is_hot")
	public Integer getMhIsHot() {
		return mhIsHot;
	}
	public void setMhIsHot(Integer mhIsHot) {
		this.mhIsHot = mhIsHot;
	}
	
	@PrePersist
	public void prePersist(){
		this.setMhState(0); 
	}
}
