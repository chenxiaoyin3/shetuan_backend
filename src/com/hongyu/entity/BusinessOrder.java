package com.hongyu.entity;

import java.io.Serializable;
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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.format.annotation.DateTimeFormat;


@SuppressWarnings("serial")
@Entity
//add by gxz 20180208
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
					    "promotionId",
					   "businessOrderItems"
})
@Table(name = "hy_business_order")
public class BusinessOrder implements Serializable {
	private Long id;
	private String orderCode;
	private String orderPhone;
	private WechatAccount wechatAccount;
	private WeBusiness weBusiness;
	private BigDecimal totalMoney; //总金额 
	private BigDecimal promotionAmount; // 优惠金额
	private BigDecimal shipFee;  
	private BigDecimal shouldPayMoney;  //应付金额 =  总金额 －优惠金额＋物流费
	private BigDecimal couponMoney;    // 使用一次电子券金额
	private BigDecimal balanceMoney;   // 使用余额金额
	private BigDecimal payMoney;       // 使用微信支付金额
	private BigDecimal refoundMoney;
	private Integer orderState;
	private Date orderTime;
	private Date reviewTime;
	private Date payTime;
	private Date deliveryTime;
	private Date receiveTime;
	private Date orderCancelTime;
	private String receiverName;
	private String receiverPhone;
	private String receiverAddress;
	private String receiverRemark;
	private Integer receiveType;
	private String invoiceTitle;
	private String taxpayerCode;
	//是否统计分成
	private Boolean isBalanced;
	//modified by gxz 20180208
	private HyPromotion promotionId;
	private String couponId;
	//add by guoxinze 20180208
	private Set<BusinessOrderItem> businessOrderItems = new HashSet<>(0);
//	private List<Ship> ships = new ArrayList<>(0);
	private Ship ship;
	
	//是否由供应商发货
	private Boolean isDivided;
	
	//父订单id
	private Long parentOrderId;
	
	//是否是原始凭证
	private Boolean isShow;

	private Boolean isAppraised;
	
	//审核人
	private HyAdmin reviewer;
	
	//是否结算
	private Boolean isBalance;
	
	//完成结算时间
	private Date completeTime;
	
	//是否有效
	private Boolean isValid;

	@Column(name="audit_status")
	public Integer getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
	}

	//审核状态
	//0 未审核，1 已审核， 2 已拒审
	private Integer auditStatus;
	
	@Column(name="portal_user_id")
	public Long getPortalUserId(){
		return portalUserId;
	}
	
	public void setPortalUserId(Long portalUserId) {
		this.portalUserId = portalUserId;
	}
	
	//2019-06-22加门户用户id
	private Long portalUserId;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "order_code")
	public String getOrderCode() {
		return this.orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	@Column(name = "order_phone")
	public String getOrderPhone() {
		return this.orderPhone;
	}
	public void setOrderPhone(String orderPhone) {
		this.orderPhone = orderPhone;
	}
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="order_wechat_id")
	public WechatAccount getWechatAccount() {
		return wechatAccount;
	}

	public void setWechatAccount(WechatAccount wechatAccount) {
		this.wechatAccount = wechatAccount;
	}
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="webusiness_id")
	public WeBusiness getWeBusiness() {
		return weBusiness;
	}

	public void setWeBusiness(WeBusiness weBusiness) {
		this.weBusiness = weBusiness;
	}
	@Column(name="total_money", precision = 10)
	public BigDecimal getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(BigDecimal totalMoney) {
		this.totalMoney = totalMoney;
	}
	@Column(name="promotion_amount", precision = 10)
	public BigDecimal getPromotionAmount() {
		return promotionAmount;
	}

	public void setPromotionAmount(BigDecimal promotionAmount) {
		this.promotionAmount = promotionAmount;
	}
	@Column(name="ship_fee", precision = 10)
	public BigDecimal getShipFee() {
		return shipFee;
	}

	public void setShipFee(BigDecimal shipFee) {
		this.shipFee = shipFee;
	}
	@Column(name="shouldpay_money", precision = 10)
	public BigDecimal getShouldPayMoney() {
		return shouldPayMoney;
	}

	public void setShouldPayMoney(BigDecimal shouldPayMoney) {
		this.shouldPayMoney = shouldPayMoney;
	}
	@Column(name="coupon_money", precision = 10)
	public BigDecimal getCouponMoney() {
		return couponMoney;
	}

	public void setCouponMoney(BigDecimal couponMoney) {
		this.couponMoney = couponMoney;
	}
	@Column(name="balance_money", precision = 10)
	public BigDecimal getBalanceMoney() {
		return balanceMoney;
	}

	public void setBalanceMoney(BigDecimal balanceMoney) {
		this.balanceMoney = balanceMoney;
	}
	@Column(name="pay_money", precision = 10)
	public BigDecimal getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(BigDecimal payMoney) {
		this.payMoney = payMoney;
	}
	@Column(name="refound_money", precision = 10)
	public BigDecimal getRefoundMoney() {
		return refoundMoney;
	}

	public void setRefoundMoney(BigDecimal refoundMoney) {
		this.refoundMoney = refoundMoney;
	}

	@Column(name = "order_state")
	public Integer getOrderState() {
		return this.orderState;
	}

	public void setOrderState(Integer orderState) {
		this.orderState = orderState;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "order_time", length = 19)
	@DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
	public Date getOrderTime() {
		return this.orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "review_time", length = 19)
	public Date getReviewTime() {
		return this.reviewTime;
	}

	public void setReviewTime(Date reviewTime) {
		this.reviewTime = reviewTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pay_time", length = 19)
	public Date getPayTime() {
		return this.payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "delivery_time", length = 19)
	public Date getDeliveryTime() {
		return this.deliveryTime;
	}

	public void setDeliveryTime(Date deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "receive_time", length = 19)
	public Date getReceiveTime() {
		return this.receiveTime;
	}

	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "order_cancel_time", length = 19)
	public Date getOrderCancelTime() {
		return this.orderCancelTime;
	}

	public void setOrderCancelTime(Date orderCancelTime) {
		this.orderCancelTime = orderCancelTime;
	}

	@Column(name = "receiver_name")
	public String getReceiverName() {
		return this.receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	@Column(name = "receiver_phone")
	public String getReceiverPhone() {
		return this.receiverPhone;
	}

	public void setReceiverPhone(String receiverPhone) {
		this.receiverPhone = receiverPhone;
	}
	@Column(name="receiver_address")
	public String getReceiverAddress() {
		return receiverAddress;
	}

	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}

	@Column(name = "receiver_remark")
	public String getReceiverRemark() {
		return this.receiverRemark;
	}

	public void setReceiverRemark(String receiverRemark) {
		this.receiverRemark = receiverRemark;
	}

	@Column(name = "receive_type")
	public Integer getReceiveType() {
		return this.receiveType;
	}

	public void setReceiveType(Integer receiveType) {
		this.receiveType = receiveType;
	}

	@Column(name = "invoice_title")
	public String getInvoiceTitle() {
		return this.invoiceTitle;
	}

	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}

	@Column(name = "taxpayer_code")
	public String getTaxpayerCode() {
		return this.taxpayerCode;
	}

	public void setTaxpayerCode(String taxpayerCode) {
		this.taxpayerCode = taxpayerCode;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "promotion_id")
	public HyPromotion getPromotionId() {
		return this.promotionId;
	}

	public void setPromotionId(HyPromotion promotionId) {
		this.promotionId = promotionId;
	}

	@Column(name = "coupon_id")
	public String getCouponId() {
		return this.couponId;
	}

	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}

	//add by gxz 20180228
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "businessOrder", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("id asc")
	public Set<BusinessOrderItem> getBusinessOrderItems() {
		return businessOrderItems;
	}

	public void setBusinessOrderItems(Set<BusinessOrderItem> businessOrderItems) {
		this.businessOrderItems = businessOrderItems;
	}
	//add by gxz 20180228

//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "orderId", cascade = CascadeType.ALL, orphanRemoval = true)
//	@OrderBy("id asc")
//	public List<Ship> getShips() {
//		return ships;
//	}
//
//	public void setShips(List<Ship> ships) {
//		this.ships = ships;
//	}
	
	
	@OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name="ship_id")
	public Ship getShip() {
		return ship;
	}

	public void setShip(Ship ship) {
		this.ship = ship;
	}
	
	@Column(name = "is_balanced")
	public Boolean getIsBalanced() {
		return isBalanced;
	}

	public void setIsBalanced(Boolean isBalanced) {
		this.isBalanced = isBalanced;
	}
	
	@PrePersist
	public void setPrepersist() {
		if(this.isBalanced==null) {
			this.isBalanced = false;
		}
		if(this.isBalance==null) {
			this.isBalance = false;
		}
		if(this.isShow==null) {
			this.isShow = false;
		}
		if(this.isAppraised==null) {
			this.isAppraised = false;
		}
		if(this.isValid==null) {
			this.isValid = true;
		}
		
		if(this.isDivided==null) {
			this.isDivided = false;
		}
		if(this.auditStatus == null){
			this.auditStatus = 0;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BusinessOrder other = (BusinessOrder) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Column(name="is_divided")
	public Boolean getIsDivided() {
		return isDivided;
	}

	public void setIsDivided(Boolean isDivided) {
		this.isDivided = isDivided;
	}

	@Column(name="parent_order_id")
	public Long getParentOrderId() {
		return parentOrderId;
	}

	public void setParentOrderId(Long parentOrderId) {
		this.parentOrderId = parentOrderId;
	}

	@Column(name="is_show")
	public Boolean getIsShow() {
		return isShow;
	}

	public void setIsShow(Boolean isShow) {
		this.isShow = isShow;
	}
	@Column(name="is_appraised")
	public Boolean getIsAppraised() {
		return isAppraised;
	}

	public void setIsAppraised(Boolean isAppraised) {
		this.isAppraised = isAppraised;
	}

	
	@ManyToOne
	@JoinColumn(name="reviewer")
	public HyAdmin getReviewer() {
		return reviewer;
	}

	public void setReviewer(HyAdmin reviewer) {
		this.reviewer = reviewer;
	}

	@Column(name="is_balance")
	public Boolean getIsBalance() {
		return isBalance;
	}

	public void setIsBalance(Boolean isBalance) {
		this.isBalance = isBalance;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="complete_time",length=19)
	@DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
	public Date getCompleteTime() {
		return completeTime;
	}

	public void setCompleteTime(Date completeTime) {
		this.completeTime = completeTime;
	}

	@Column(name="is_valid")
	public Boolean getIsValid() {
		return isValid;
	}

	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}
	
}


