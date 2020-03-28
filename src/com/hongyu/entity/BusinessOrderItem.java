package com.hongyu.entity;

import java.beans.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"
	})
@Table(name = "hy_business_order_item")
public class BusinessOrderItem implements Serializable {
	private Long id;
	private BusinessOrder businessOrder;
	private Long specialty;
	private Long specialtySpecification;
	private PurchaseItem purchaseItem;
	private Integer quantity;
	private Integer outboundQuantity;
	private Date outboundTime;
	private Date createTime;
	private String operator;
	private Integer returnQuantity;
	private BigDecimal salePrice;
	private BigDecimal originalPrice;
	private Boolean isappraised;
	private HyPromotion promotionId;
	private Integer type;
	
	/**BusinessOrdrItem  是否退货*/
	private Integer isDelivered;
	
	
	//发货商名称
	private String deliverName;
	
	//发货商类型
	private Integer deliverType;
	
	//仓库名称
	private String depotName;
	
	//售后计损数量
	private Double lost1Quantity;
	
	//库管计损数量
	private Double lost2Quantity;
	
	//是否是赠品
	private Boolean isGift;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	public BusinessOrder getBusinessOrder() {
		return businessOrder;
	}

	public void setBusinessOrder(BusinessOrder businessOrder) {
		this.businessOrder = businessOrder;
	}

	@Column(name = "specialty_id")
	public Long getSpecialty() {
		return specialty;
	}

	public void setSpecialty(Long specialty) {
		this.specialty = specialty;
	}

	@Column(name = "specialty_specification_id")
	public Long getSpecialtySpecification() {
		return specialtySpecification;
	}

	public void setSpecialtySpecification(Long specialtySpecification) {
		this.specialtySpecification = specialtySpecification;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "purchase_item_id")
	public PurchaseItem getPurchaseItem() {
		return purchaseItem;
	}

	public void setPurchaseItem(PurchaseItem purchaseItem) {
		this.purchaseItem = purchaseItem;
	}

	@Column(name = "quantity")
	public Integer getQuantity() {
		return this.quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Column(name = "sale_price", precision = 20)
	public BigDecimal getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(BigDecimal salePrice) {
		this.salePrice = salePrice;
	}

	@Column(name = "original_price", precision = 20)
	public BigDecimal getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(BigDecimal originalPrice) {
		this.originalPrice = originalPrice;
	}

	public Boolean getIsappraised() {
		return isappraised;
	}

	public void setIsappraised(Boolean isappraised) {
		this.isappraised = isappraised;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "promotion_id")
	public HyPromotion getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(HyPromotion promotionId) {
		this.promotionId = promotionId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "is_delivered")
	public Integer getIsDelivered() {
		return isDelivered;
	}

	public void setIsDelivered(Integer isDelivered) {
		this.isDelivered = isDelivered;
	}

	@Column(name="outbound_quantity")
	public Integer getOutboundQuantity() {
		return outboundQuantity;
	}

	public void setOutboundQuantity(Integer outboundQuantity) {
		this.outboundQuantity = outboundQuantity;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "outbound_time", length = 19)
	public Date getOutboundTime() {
		return outboundTime;
	}

	public void setOutboundTime(Date outboundTime) {
		this.outboundTime = outboundTime;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", length = 19)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name="operator")
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}

	@Column(name="return_quantity")
	public Integer getReturnQuantity() {
		return returnQuantity;
	}

	public void setReturnQuantity(Integer returnQuantity) {
		this.returnQuantity = returnQuantity;
	}

	@Column(name="deliver_name")
	public String getDeliverName() {
		return deliverName;
	}

	public void setDeliverName(String deliverName) {
		this.deliverName = deliverName;
	}

	@Column(name="deliver_type")
	public Integer getDeliverType() {
		return deliverType;
	}

	public void setDeliverType(Integer deliverType) {
		this.deliverType = deliverType;
	}

	@Column(name="depot_name")
	public String getDepotName() {
		return depotName;
	}

	public void setDepotName(String depotName) {
		this.depotName = depotName;
	}

	@PrePersist
	public void setPrepersist() {
		if(this.returnQuantity==null) {
			this.returnQuantity = 0;
		}
		this.createTime = new Date();
		if(this.lost1Quantity==null) {
			this.lost1Quantity = 0.0;
		}
		if(this.lost2Quantity==null) {
			this.lost2Quantity = 0.0;
		}
		if(this.isGift==null) {
			this.isGift=false;
		}
		
	}

	@Column(name="lost1_quantity")
	public Double getLost1Quantity() {
		return lost1Quantity;
	}

	public void setLost1Quantity(Double lost1Quantity) {
		this.lost1Quantity = lost1Quantity;
	}

	@Column(name="lost2_quantity")
	public Double getLost2Quantity() {
		return lost2Quantity;
	}

	public void setLost2Quantity(Double lost2Quantity) {
		this.lost2Quantity = lost2Quantity;
	}

	@Column(name="is_gift")
	public Boolean getIsGift() {
		return isGift;
	}

	public void setIsGift(Boolean isGift) {
		this.isGift = isGift;
	}
}